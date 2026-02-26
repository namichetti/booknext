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
public class BookUpdateDto {

    private String title;

    private String description;

    @Positive(message = "El número de edición debe ser positivo")
    private Integer editionNumber;

    private String dimensions;

    @Positive(message = "El número de páginas debe ser positivo")
    private Integer pageCount;

    @Positive(message = "El peso debe ser positivo")
    private Double weight;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Positive(message = "El precio debe ser positivo")
    private BigDecimal price;

    @Valid
    private BookCategoryRequestDto bookCategory;

    @Builder.Default
    private Set<@Valid AuthorRequestDto> authorRequestDtos = new HashSet<>();

    @Valid
    private PublisherRequestDto publisherRequest;
}

