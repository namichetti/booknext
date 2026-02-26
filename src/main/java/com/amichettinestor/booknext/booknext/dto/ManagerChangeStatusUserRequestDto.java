package com.amichettinestor.booknext.booknext.dto;

import com.amichettinestor.booknext.booknext.enums.ManagerChangeUserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerChangeStatusUserRequestDto {

    @NotNull(message = "El estado es obligatorio")
    private ManagerChangeUserStatus status;
}
