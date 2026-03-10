package com.amichettinestor.booknext.booknext.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    private String name;

    @NotBlank(message = "El nombre del país es obligatorio")
    private String countryName;
}

