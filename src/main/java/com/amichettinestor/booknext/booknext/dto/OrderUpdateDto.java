package com.amichettinestor.booknext.booknext.dto;

import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto {

    private OrderStatus status;

    @NotEmpty(message = "La orden debe contener al menos un item")
    @Valid
    @Builder.Default
    private Set<OrderItemUpdateDto> items = new HashSet<>();
}

