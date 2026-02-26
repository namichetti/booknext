package com.amichettinestor.booknext.booknext.dto;

import com.amichettinestor.booknext.booknext.enums.AdminChangeUserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangeStatusUserRequestDto {

    @NotNull(message = "El estado no puede ser nulo")
    private AdminChangeUserStatus status;

}
