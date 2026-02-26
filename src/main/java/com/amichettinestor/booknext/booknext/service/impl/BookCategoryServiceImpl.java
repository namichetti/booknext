package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.BookCategory;
import com.amichettinestor.booknext.booknext.exception.BookCategoryNotFoundException;
import com.amichettinestor.booknext.booknext.repository.BookCategoryRepository;
import com.amichettinestor.booknext.booknext.service.BookCategoryService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookCategoryResponseDto> findAll() {
        var categories = this.bookCategoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    Set<BookResponseDto> booksDto = category.getBooks().stream()
                            .map(book -> BookResponseDto.builder()
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
                                            .map(author -> author.getName()+
                                                    " "+author.getLastName())
                                            .collect(Collectors.toSet()))
                                    .build())
                            .collect(Collectors.toSet());

                    return BookCategoryResponseDto.builder()
                            .description(category.getDescription())
                            .bookResponseDtos(booksDto)
                            .id(category.getId())
                            .build();
                }).toList();
    }


    @Override
    public void deleteById(Long id) {
        this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        this.bookCategoryRepository.deleteById(id);
    }

    @Override
    public void save(BookCategoryRequestDto bookCategoryRequestDto) {
        var category= BookCategory.builder()
                .description(bookCategoryRequestDto.getDescription())
                .build();

        this.bookCategoryRepository.save(category);
    }

    @Override
    public void patchBookCategory(Long id, BookCategoryRequestDto bookCategoryRequestDto) {
        var category= this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));

        //Clase para actualizar solo los campos que hay que actualizar.
        PatchUtils.copyNonNullProperties(bookCategoryRequestDto, category);

        this.bookCategoryRepository.save(category);
    }

    @Override
    public void putBookCategory(Long id, BookCategoryRequestDto bookUpdateDto) {
        var category = this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));

        category.setDescription(bookUpdateDto.getDescription());

        this.bookCategoryRepository.save(category);

    }

    @Override
    @Transactional(readOnly=true)
    public BookCategoryResponseDto findById(Long id) {
        var category = this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));

        var bookResponseDtos= category.getBooks().stream()
                .map(book -> BookResponseDto.builder()
                        .isbn(book.getIsbn())
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
                                .map(author -> author.getName()+
                                        " "+author.getLastName())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());

        return BookCategoryResponseDto.builder()
                .description(category.getDescription())
                .bookResponseDtos(bookResponseDtos)
                .id(category.getId())
                .build();
    }

    @Override
    @Transactional(readOnly=true)
    public List<BookCategoryResponseDto> findByBookCategoryDescription(String description) {
        var categories = this.bookCategoryRepository.findByDescriptionContainingIgnoreCase(description);

        return categories.stream()
                .map(category -> BookCategoryResponseDto.builder()
                        .description(category.getDescription())
                        .bookResponseDtos(category.getBooks().stream()
                                .map(book -> BookResponseDto.builder()
                                        .isbn(book.getIsbn())
                                        .title(book.getTitle())
                                        .description(book.getDescription())
                                        .editionNumber(book.getEditionNumber())
                                        .dimensions(book.getDimensions())
                                        .pageCount(book.getPageCount())
                                        .weight(book.getWeight())
                                        .stock(book.getStock())
                                        .price(book.getPrice())
                                        .bookCategory(book.getBookCategory().getDescription())
                                        .authors(book.getAuthors().stream()
                                                .map(author -> author.getName()+
                                                        " "+author.getLastName())
                                                .collect(Collectors.toSet()))
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .toList();

    }
}
