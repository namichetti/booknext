package com.amichettinestor.booknext.booknext.dto;

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
public class BookResponseDto {

    private Long id;
    private String isbn;
    private String title;
    private String description;
    private Integer editionNumber;
    private String dimensions;
    private Integer pageCount;
    private Double weight;
    private Integer stock;
    private BigDecimal price;
    private String bookCategory;
    @Builder.Default
    private Set<String> authors = new HashSet<>();
    private String publisher;

}
