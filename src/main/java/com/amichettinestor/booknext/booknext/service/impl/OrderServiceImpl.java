package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Book;
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

    @Override
    @Transactional
    public void save(OrderRequestDto orderRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró al usuario " + username));

        // Buscar carrito activo (orden con estado PROCESSING)
        Optional<Order> cart = orderRepository.findByUserAndStatus(user, OrderStatus.PROCESSING);

        Order order = cart.orElseGet(() -> Order.builder()
                .user(user)
                .status(OrderStatus.PROCESSING)
                .items(new HashSet<>())
                .build());

        for (var itemDto : orderRequestDto.getItems()) {

            var book = bookRepository.findByIsbn(itemDto.getIsbn())
                    .orElseThrow(() ->
                            new BookNotFoundException("Libro no encontrado: " + itemDto.getIsbn()));

            // Buscar si el item ya existe en la orden
            Optional<OrderItem> existingItem = order.getItems().stream()
                    .filter(i -> i.getBook().getIsbn().equalsIgnoreCase(itemDto.getIsbn()))
                    .findFirst();

            if (existingItem.isPresent()) {

                OrderItem item = existingItem.get();

                int nuevaCantidad = item.getQuantity() + itemDto.getQuantity();

                if (book.getStock() < itemDto.getQuantity()) {
                    throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                }

                book.setStock(book.getStock() - itemDto.getQuantity());

                item.setQuantity(nuevaCantidad);

            } else {

                if (book.getStock() < itemDto.getQuantity()) {
                    throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                }

                book.setStock(book.getStock() - itemDto.getQuantity());

                OrderItem newItem = OrderItem.builder()
                        .book(book)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(book.getPrice())
                        .order(order)
                        .build();

                order.getItems().add(newItem);
            }

            bookRepository.save(book);
        }

        // Recalcular total de la orden
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setFinalPrice(total);

        orderRepository.save(order);
    }


    @Override
    @Transactional
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
    @Transactional
    public void patchOrder(Long id, OrderUpdateDto orderUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdminOrManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        var order = findOrderById(id);

        if (!isAdminOrManager && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("No puede actualizar esta orden");
        }

        // Solo actualizar propiedades que no afecten stock directamente
        PatchUtils.copyNonNullProperties(orderUpdateDto, order);

        // Actualizar cantidades de items y stock
        for (OrderItemUpdateDto itemDto : orderUpdateDto.getItems()) {

            // Buscar el OrderItem correspondiente
            OrderItem orderItem = order.getItems().stream()
                    .filter(i -> i.getBook().getIsbn().equalsIgnoreCase(itemDto.getIsbn()))
                    .findFirst()
                    .orElseThrow(() -> new BookNotFoundException(
                            "El libro con ISBN " + itemDto.getIsbn() + " no está en la orden"));

            var book = orderItem.getBook();

            int cantidadAnterior = orderItem.getQuantity();
            int cantidadNueva = itemDto.getQuantity();
            int diferencia = cantidadNueva - cantidadAnterior; // positiva = resta stock, negativa = devuelve stock

            // Validar stock solo si estamos aumentando la cantidad
            if (diferencia > 0 && book.getStock() < diferencia) {
                throw new StockException("Stock insuficiente para el libro " + book.getTitle());
            }

            // Ajustar stock
            book.setStock(book.getStock() - diferencia);

            // Actualizar cantidad en OrderItem
            orderItem.setQuantity(cantidadNueva);

            // Guardar cambios en el libro
            bookRepository.save(book);
        }

        // Recalcular total de la orden
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setFinalPrice(total);

        // Guardar la orden con los cambios
        orderRepository.save(order);
    }

    @Override
    @Transactional
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

        // --- 1. Devolver stock de los items antiguos y limpiar la colección ---
        order.getItems().stream()
                .peek(oldItem -> {
                    Book book = oldItem.getBook();
                    book.setStock(book.getStock() + oldItem.getQuantity());
                    bookRepository.saveAndFlush(book);
                })
                .forEach(oldItem -> oldItem.setOrder(null)); // rompe la referencia bidireccional

        order.getItems().clear(); // limpieza segura después de romper referencias

        // --- 2. Agregar y actualizar items nuevos desde el DTO ---
        Set<OrderItem> newItems = orderUpdateDto.getItems().stream()
                .map(itemDto -> {
                    Book book = bookRepository.findByIsbn(itemDto.getIsbn())
                            .orElseThrow(() -> new BookNotFoundException(
                                    "Libro no encontrado: " + itemDto.getIsbn()));

                    if (book.getStock() < itemDto.getQuantity()) {
                        throw new StockException("Stock insuficiente para el libro " + book.getTitle());
                    }

                    book.setStock(book.getStock() - itemDto.getQuantity());
                    bookRepository.saveAndFlush(book);

                    return OrderItem.builder()
                            .book(book)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(book.getPrice())
                            .order(order)
                            .build();
                })
                .collect(Collectors.toSet());

        newItems.forEach(order::addItem); // agrega items manteniendo la relación bidireccional


        // --- 4. Recalcular total de la orden ---
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setFinalPrice(total);

        // --- 5. Guardar la orden con todos los cambios ---
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
