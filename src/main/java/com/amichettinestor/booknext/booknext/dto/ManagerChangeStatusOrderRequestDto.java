package com.amichettinestor.booknext.booknext.dto;

import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerChangeStatusOrderRequestDto {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

}
