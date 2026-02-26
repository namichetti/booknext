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
public class PublisherRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
}
