package com.amichettinestor.booknext.booknext.dto;

import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.entity.User;
import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalPrice;
    @Builder.Default
    private Set<OrderItemResponseDto> items = new HashSet<>();
}
