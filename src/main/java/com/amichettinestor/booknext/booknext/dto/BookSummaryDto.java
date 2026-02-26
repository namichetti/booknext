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
public class BookSummaryDto {

    //@NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    //@NotBlank(message = "El título es obligatorio")
    private String title;
}
