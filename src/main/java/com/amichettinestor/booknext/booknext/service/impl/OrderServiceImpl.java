package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.entity.Order;
import com.amichettinestor.booknext.booknext.entity.OrderItem;
import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import com.amichettinestor.booknext.booknext.enums.Role;
import com.amichettinestor.booknext.booknext.exception.*;
import com.amichettinestor.booknext.booknext.mapper.OrderMapper;
import com.amichettinestor.booknext.booknext.repository.BookRepository;
import com.amichettinestor.booknext.booknext.repository.OrderItemRepository;
import com.amichettinestor.booknext.booknext.repository.OrderRepository;
import com.amichettinestor.booknext.booknext.repository.UserRepository;
import com.amichettinestor.booknext.booknext.service.OrderService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void save(List<OrderRequestDto> orderRequestDtos) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró al usuario " + username));

        // Buscar carrito activo
        var order = orderRepository.findByUserAndStatus(user, OrderStatus.PROCESSING)
                .orElseGet(() -> Order.builder()
                        .user(user)
                        .status(OrderStatus.PROCESSING)
                        .items(new ArrayList<>())
                        .build());

        for (OrderRequestDto request : orderRequestDtos) {

            for (OrderItemRequestDto itemDto : request.getItems()) {

                var book = bookRepository.findByIsbn(itemDto.getIsbn())
                        .orElseThrow(() -> new BookNotFoundException(
                                "Libro no encontrado: " + itemDto.getIsbn()
                        ));

                Optional<OrderItem> existingItem = order.getItems().stream()
                        .filter(i -> i.getBook().getIsbn().equalsIgnoreCase(itemDto.getIsbn()))
                        .findFirst();

                int newQuantity = itemDto.getQuantity();

                if (existingItem.isPresent()) {

                    OrderItem item = existingItem.get();
                    int oldQuantity = item.getQuantity();
                    int difference = newQuantity - oldQuantity;

                    // Validar stock SOLO si aumenta
                    if (difference > 0 && book.getStock() < difference) {
                        throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                    }

                    // Ajustar stock correctamente
                    book.setStock(book.getStock() - difference);

                    // Actualizar cantidad
                    item.setQuantity(newQuantity);

                } else {

                    // Nuevo item
                    if (book.getStock() < newQuantity) {
                        throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                    }

                    book.setStock(book.getStock() - newQuantity);

                    OrderItem newItem = OrderItem.builder()
                            .book(book)
                            .quantity(newQuantity)
                            .unitPrice(book.getPrice())
                            .order(order)
                            .build();

                    order.getItems().add(newItem);
                }

                bookRepository.save(book);
            }
        }

        // Recalcular total
        order.recalculateTotal();

        orderRepository.save(order);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = this.userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("No se encontró al usuario "
                        + username));
        var order = this.findOrderById(id);

        if(!user.getOrders().contains(order)) {
            if(!user.getRole().equals(Role.ADMIN)){
                throw new OrderNotBelongToException("La orden "+order.getId()+ " " +
                        "no pertenece al usuario "+username);
            }
        }

        // Devolvemos stock al eliminar la orden.
        order.getItems().forEach(orderItem -> {
            var book = orderItem.getBook();
            book.setStock(book.getStock() + orderItem.getQuantity());
            bookRepository.save(book);
        });
        user.getOrders().remove(order);

        orderRepository.deleteById(order.getId());
    }

    @Override
    @Transactional
    public void patchOrder(Long id, OrderUpdateDto orderUpdateDto) {
        var order = this.findOrderById(id);

        orderUpdateDto.getItems().forEach(orderItem -> {

            var book = this.orderItemRepository.findBookByIsbn(orderItem.getIsbn())
                    .orElseThrow(() -> new BookNotFoundException(
                            "El libro con ISBN "
                                    + orderItem.getIsbn() + " no se encontró."));

            order.getItems().forEach(orderItem1 -> {

                if (orderItem1.getBook().getIsbn().equals(book.getIsbn())) {

                    int oldQuantity = orderItem1.getQuantity();
                    int newQuantity = orderItem.getQuantity();

                    int difference = newQuantity - oldQuantity;

                    // Si quiere más unidades → verificar stock
                    if (difference > 0) {
                        if (book.getStock() < difference) {
                            throw new StockException("Stock insuficiente");
                        }
                        book.setStock(book.getStock() - difference);
                    }

                    // Si quiere menos unidades → devolver stock
                    if (difference < 0) {
                        book.setStock(book.getStock() + Math.abs(difference));
                    }

                    // Actualizar order item
                    orderItem1.setQuantity(newQuantity);

                    this.orderItemRepository.save(orderItem1);
                    this.bookRepository.save(book);
                }
            });

        });
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAll() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Order> orders;
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_MANAGER"))) {
            orders = orderRepository.findAll(); // ADMIN y MANAGER pueden ver todas las órdenes
        } else {
            orders = orderRepository.findByUserUsername(username); // USER solo sus órdenes
        }

        return this.orderMapper.toDtoList(orders);
    }

    @Override
    @Transactional(readOnly=true)
    public OrderResponseDto findById(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = this.userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("No se encontró al usuario "
                        + username));

        var order = this.findOrderById(id);

        if(!user.getOrders().contains(order)) {
            if(!user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.MANAGER)){
                throw new OrderNotBelongToException("La orden "+order.getId()+ " " +
                        "no pertenece al usuario "+username);
            }

        }
        return this.orderMapper.toDto(order);
    }


    @Override
    @Transactional
    public void changeStatus(Long id, ChangeStatusOrderRequestDto statusOrderRequestDto) {
        var order = findOrderById(id);
        var requestedStatus = statusOrderRequestDto.getStatus();
        validateStatus(order,requestedStatus);

    }

    private Order findOrderById(Long id){
        return this.orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("No se encontró la orden con id "+id));
    }

    private void validateStatus(Order order,OrderStatus requestedStatus){

        if (requestedStatus != OrderStatus.SUSPENDED &&
                requestedStatus != OrderStatus.COMPLETED &&
                requestedStatus != OrderStatus.PROCESSING) {
            throw new OrderStatusException("El estado solicitado no puede ser asignado");
        }

        OrderStatus newStatus;
        if (requestedStatus == OrderStatus.SUSPENDED) {
            newStatus = OrderStatus.SUSPENDED;
        } else if(requestedStatus == OrderStatus.PROCESSING) {
            newStatus = OrderStatus.PROCESSING;
        }else{
            newStatus = OrderStatus.COMPLETED;
        }

        order.setStatus(newStatus);

        this.orderRepository.save(order);
    }


}
