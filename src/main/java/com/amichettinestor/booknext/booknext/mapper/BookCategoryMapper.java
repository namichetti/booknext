package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.BookCategory;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookCategoryMapper {

    public BookCategoryResponseDto toDto(BookCategory bookCategory) {
        return BookCategoryResponseDto.builder()
                .description(bookCategory.getDescription())
                .id(bookCategory.getId())
                .build();
    }

    public BookCategory toEntity(BookCategoryRequestDto bookCategoryRequestDto) {
        return BookCategory.builder()
                .description(bookCategoryRequestDto.getDescription())
                .build();
    }

    public void patchFromDto(BookCategory bookCategory, BookCategoryRequestDto bookCategoryRequestDto) {
        PatchUtils.copyNonNullProperties(bookCategoryRequestDto, bookCategory);
    }

    public BookCategory putFromDto(BookCategory bookCategory, BookCategoryRequestDto bookUpdateDto) {
        bookCategory.setDescription(bookUpdateDto.getDescription());
        return bookCategory;
    }


    public List<BookCategoryResponseDto> toDtoList(List<BookCategory> categories) {
        return categories.stream()
                .map(category -> {
                    Set<BookResponseDto> booksDto = category.getBooks().stream()
                            .map(book -> BookResponseDto.builder()
                                    .id(book.getId())
                                    .isbn(book.getIsbn())
                                    .title(book.getTitle())
                                    .description(book.getDescription())
                                    .editionNumber(book.getEditionNumber())
                                    .dimensions(book.getDimensions())
                                    .pageCount(book.getPageCount())
                                    .weight(book.getWeight())
                                    .stock(book.getStock())
                                    .price(book.getPrice())
                                    .publisher(book.getPublisher().getName())
                                    .bookCategory(book.getBookCategory().getDescription())
                                    .authors(book.getAuthors().stream()
                                            .map(author -> author.getName() +
                                                    " " + author.getLastName())
                                            .collect(Collectors.toSet()))
                                    .build())
                            .collect(Collectors.toSet());

                    return BookCategoryResponseDto.builder()
                            .description(category.getDescription())
                            .id(category.getId())
                            .build();
                }).toList();
    }

    public BookCategoryBooksResponseDto toDtoWithBooks(BookCategory category) {
        var bookResponseDtos = category.getBooks().stream()
                .map(book -> BookResponseDto.builder()
                        .isbn(book.getIsbn())
                        .id(book.getId())
                        .title(book.getTitle())
                        .description(book.getDescription())
                        .editionNumber(book.getEditionNumber())
                        .dimensions(book.getDimensions())
                        .pageCount(book.getPageCount())
                        .weight(book.getWeight())
                        .stock(book.getStock())
                        .price(book.getPrice())
                        .bookCategory(book.getBookCategory().getDescription())
                        .publisher(book.getPublisher().getName())
                        .authors(book.getAuthors().stream()
                                .map(author -> author.getName() +
                                        " " + author.getLastName())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());

        return BookCategoryBooksResponseDto.builder()
                .description(category.getDescription())
                .id(category.getId())
                .bookResponseDtos(bookResponseDtos)
                .build();
    }

    public List<BookCategoryBooksResponseDto> toDtoListWithBooks(List<BookCategory> categories) {
        return categories.stream()
                .map(category -> BookCategoryBooksResponseDto.builder()
                        .description(category.getDescription())
                        .id(category.getId())
                        .bookResponseDtos(category.getBooks().stream()
                                .map(book -> BookResponseDto.builder()
                                        .isbn(book.getIsbn())
                                        .id(book.getId())
                                        .title(book.getTitle())
                                        .description(book.getDescription())
                                        .editionNumber(book.getEditionNumber())
                                        .dimensions(book.getDimensions())
                                        .pageCount(book.getPageCount())
                                        .weight(book.getWeight())
                                        .stock(book.getStock())
                                        .price(book.getPrice())
                                        .publisher(book.getPublisher().getName())
                                        .bookCategory(book.getBookCategory().getDescription())
                                        .authors(book.getAuthors().stream()
                                                .map(author -> author.getName() +
                                                        " " + author.getLastName())
                                                .collect(Collectors.toSet()))
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }
}
