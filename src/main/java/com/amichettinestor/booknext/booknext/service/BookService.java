package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.BookRequestDto;
import com.amichettinestor.booknext.booknext.dto.BookResponseDto;
import com.amichettinestor.booknext.booknext.dto.BookUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {

    List<BookResponseDto> findAll();

    BookResponseDto findById(Long id);

    void save(BookRequestDto bookRequestDto);

    void deleteById(Long id);

    void patchBook(Long id, BookUpdateDto bookUpdateDto);

    void putBook(Long id, BookUpdateDto bookUpdateDto);

    List<BookResponseDto> searchByText(String text);

    List<BookResponseDto> searchByPriceRange(BigDecimal min, BigDecimal max);
}
