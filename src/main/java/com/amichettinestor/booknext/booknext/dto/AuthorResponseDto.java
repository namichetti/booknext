package com.amichettinestor.booknext.booknext.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorResponseDto extends PersonDto{

    private Long id;
    private Set<BookSummaryDto> books = new HashSet<>();

}
