package com.amichettinestor.booknext.booknext.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descrición es obligatorio")
    private String description;

    @NotNull(message = "El número de edición es obligatorio")
    @Positive(message = "El número de edición debe ser positivo")
    private Integer editionNumber;

    private String dimensions;

    @NotNull(message = "El número de páginas es obligatorio")
    @Positive(message = "El número de páginas debe ser positivo")
    private Integer pageCount;

    @Positive(message = "El peso debe ser positivo")
    private Double weight;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal price;

    @NotNull(message = "La categoría del libro es obligatoria")
    @Valid
    private BookCategoryRequestDto bookCategory;

    @NotEmpty(message = "Debe haber al menos un autor")
    //Set<@NotBlank(...)> valida cada elemento del set, asegurando que no haya strings vacíos.
    @Builder.Default
    private Set<@NotBlank(message = "El nombre del autor no puede estar vacío") String> authorNames = new HashSet<>();

    @NotNull(message = "El publisher es obligatorio")
    @Valid
    private PublisherRequestDto publisherRequest;
}

