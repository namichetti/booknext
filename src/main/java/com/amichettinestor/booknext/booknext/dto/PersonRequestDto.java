package com.amichettinestor.booknext.booknext.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotNull(message = "La ubicación es obligatoria")
    @Valid
    private LocationRequestDto location;
}
