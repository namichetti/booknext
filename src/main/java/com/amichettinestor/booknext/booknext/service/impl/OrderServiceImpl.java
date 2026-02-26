package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Order;
import com.amichettinestor.booknext.booknext.entity.OrderItem;
import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import com.amichettinestor.booknext.booknext.exception.*;
import com.amichettinestor.booknext.booknext.repository.BookRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public void save(OrderRequestDto orderRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró al usuario " + username));

        var items = orderRequestDto.getItems().stream()
                .map(itemDto -> {
                    var book = bookRepository.findByIsbn(itemDto.getIsbn())
                            .orElseThrow(() ->
                                    new BookNotFoundException("Libro no encontrado: " + itemDto.getIsbn()));

                    if (book.getStock() < itemDto.getQuantity()) {
                        throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                    }

                    // Restar stock
                    book.setStock(book.getStock() - itemDto.getQuantity());
                    bookRepository.save(book);

                    // Crear OrderItem
                    return OrderItem.builder()
                            .book(book)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(book.getPrice())
                            .build();
                })
                .collect(Collectors.toSet());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PROCESSING)
                .items(new HashSet<>())
                .build();

        // Asocia items a la orden
        items.forEach(order::addItem);

        // Guardar orden (cascade persiste también los OrderItem)
        orderRepository.save(order);
    }


    @Override
    public void deleteById(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var order = this.findOrderById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("No puede eliminar esta orden");
        }

        // Devolvemos stock al eliminar la orden.
        order.getItems().forEach(orderItem -> {
            var book = orderItem.getBook();
            book.setStock(book.getStock() + orderItem.getQuantity());
            bookRepository.save(book);
        });

        // Eliminar la orden (cascade elimina también los OrderItem)
        orderRepository.delete(order);
    }

    @Override
    public void patchOrder(Long id, OrderUpdateDto orderUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdminOrManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        var order = findOrderById(id);

        if (!isAdminOrManager && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("No puede actualizar esta orden");
        }

        PatchUtils.copyNonNullProperties(orderUpdateDto, order);

        this.orderRepository.save(order);
    }

    @Override
    public void putOrder(Long id, OrderUpdateDto orderUpdateDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var order = findOrderById(id);

        // Validación de permisos
        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("No puede actualizar esta orden");
        }

        // Actualizar estado
        order.setStatus(orderUpdateDto.getStatus());

        // Procesar items
        // Primero devolvemos stock de los items existentes
        order.getItems().forEach(orderItem -> {
            var book = orderItem.getBook();
            book.setStock(book.getStock() + orderItem.getQuantity());
            bookRepository.save(book);
        });

        // Limpiamos los items existentes
        order.getItems().clear();

        var newItems = orderUpdateDto.getItems().stream()
                .map(itemDto -> {
                    var book = bookRepository.findByIsbn(itemDto.getIsbn())
                            .orElseThrow(() -> new BookNotFoundException("Libro no encontrado: " + itemDto.getIsbn()));

                    if (book.getStock() < itemDto.getQuantity()) {
                        throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                    }

                    // Restamos stock
                    book.setStock(book.getStock() - itemDto.getQuantity());
                    bookRepository.save(book);

                    return OrderItem.builder()
                            .book(book)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(book.getPrice())
                            .build();
                })
                .collect(Collectors.toSet());

        // Asociar items a la orden y recalcular total
        newItems.forEach(order::addItem);

        // Guardar la orden (cascade persiste los OrderItem)
        orderRepository.save(order);
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

        return orders.stream()
                .map(order -> OrderResponseDto.builder()
                        .id(order.getId())
                        .orderDate(order.getOrderDate())
                        .status(order.getStatus())
                        .totalPrice(order.getFinalPrice())
                        .items(order.getItems().stream()
                                .map(item -> OrderItemResponseDto.builder()
                                        .isbn(item.getBook().getIsbn())
                                        .title(item.getBook().getTitle())
                                        .unitPrice(item.getUnitPrice())
                                        .quantity(item.getQuantity())
                                        .subtotal(item.getSubtotal())
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly=true)
    public OrderResponseDto findById(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var order = findOrderById(id);

        // Validar si el usuario puede acceder
        boolean isAdminOrManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_MANAGER"));

        if (!isAdminOrManager && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("No puede acceder a esta orden");
        }

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalPrice(order.getFinalPrice())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponseDto.builder()
                                .isbn(item.getBook().getIsbn())
                                .title(item.getBook().getTitle())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }


    @Override
    public void changeStatusByManager(Long id, ManagerChangeStatusOrderRequestDto statusOrderRequestDto) {
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
            throw new AdminChangeStatusException("El estado solicitado no puede ser asignado");
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
