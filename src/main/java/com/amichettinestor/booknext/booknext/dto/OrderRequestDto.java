package com.amichettinestor.booknext.booknext.dto;

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
public class OrderRequestDto {


    @NotEmpty(message = "La orden debe contener al menos un item")
    @Valid
    @Builder.Default
    private Set<OrderItemRequestDto> items = new HashSet<>();
}

