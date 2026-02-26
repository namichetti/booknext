package com.amichettinestor.booknext.booknext.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryRequestDto {

    @NotBlank(message = "La descripción de la categoría del libro es obligatoria")
    private String description;
}
