package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.BookCategoryRequestDto;
import com.amichettinestor.booknext.booknext.dto.BookCategoryResponseDto;

import java.util.List;

public interface BookCategoryService {
    List<BookCategoryResponseDto> findAll();

    void deleteById(Long id);

    void save(BookCategoryRequestDto bookCategoryRequestDto);

    void patchBookCategory(Long id, BookCategoryRequestDto bookCategoryRequestDto);

    void putBookCategory(Long id, BookCategoryRequestDto bookCategoryRequestDto);

    BookCategoryResponseDto findById(Long id);

    List<BookCategoryResponseDto> findByBookCategoryDescription(String description);
}
