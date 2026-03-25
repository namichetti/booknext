package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Author;
import com.amichettinestor.booknext.booknext.entity.Location;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequestDto authorRequestDto, Author author, Location location) {
        author.setName(authorRequestDto.getName());
        author.setLastName(authorRequestDto.getLastName());
        author.setLocation(location);
        return author;
    }

    public AuthorResponseDto toDto(Author author, Location location) {
        AuthorResponseDto authorResponseDto = new AuthorResponseDto();
        authorResponseDto.setId(author.getId());
        authorResponseDto.setName(author.getName());
        authorResponseDto.setLastName(author.getLastName());
        authorResponseDto.setBooks(
                author.getBooks().stream()
                        .map(book -> BookSummaryDto.builder()
                                .isbn(book.getIsbn())
                                .title(book.getTitle())
                                .build())
                        .collect(Collectors.toSet())
        );
        authorResponseDto.setLocation(
                LocationRequestDto.builder()
                        .name(location.getName())
                        .countryName(location.getCountry().getName())
                        .build()
        );
        return authorResponseDto;
    }

    public void patchFromDto(AuthorUpdateDto dto, Author author, Location location) {
        author.setLocation(location);
        PatchUtils.copyNonNullProperties(dto, author);
    }


    public List<AuthorResponseDto> toDtoList(List<Author> authors) {
        return authors.stream()
                .map(author -> {
                    AuthorResponseDto authorResponseDto = new AuthorResponseDto();
                    authorResponseDto.setBooks(
                            author.getBooks().stream()
                                    .map(book -> BookSummaryDto.builder()
                                            .isbn(book.getIsbn())
                                            .title(book.getTitle())
                                            .build())
                                    .collect(Collectors.toSet()));
                    authorResponseDto.setName(author.getName());
                    authorResponseDto.setLastName(author.getLastName());
                    authorResponseDto.setId(author.getId());
                    authorResponseDto.setLocation(
                            LocationRequestDto.builder()
                                    .countryName(author.getLocation().getCountry().getName())
                                    .name(author.getLocation().getName())
                                    .build());
                    return authorResponseDto;
                })
                .toList();
    }
}
