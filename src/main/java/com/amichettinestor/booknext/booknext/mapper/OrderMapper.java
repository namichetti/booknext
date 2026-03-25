package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.OrderItemResponseDto;
import com.amichettinestor.booknext.booknext.dto.OrderResponseDto;
import com.amichettinestor.booknext.booknext.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDto toDto(Order order) {
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

    public List<OrderResponseDto> toDtoList(List<Order> orders){
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
