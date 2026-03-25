package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.exception.BookCategoryNotFoundException;
import com.amichettinestor.booknext.booknext.mapper.BookCategoryMapper;
import com.amichettinestor.booknext.booknext.repository.BookCategoryRepository;
import com.amichettinestor.booknext.booknext.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;
    private final BookCategoryMapper bookCategoryMapper;


    @Override
    @Transactional(readOnly = true)
    public List<BookCategoryResponseDto> findAll() {
        var categories = this.bookCategoryRepository.findAll();
        return this.bookCategoryMapper.toDtoList(categories);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        this.bookCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(BookCategoryRequestDto bookCategoryRequestDto) {

        this.bookCategoryRepository.save(this.bookCategoryMapper.toEntity(bookCategoryRequestDto));
    }

    @Override
    public void patchBookCategory(Long id, BookCategoryRequestDto bookCategoryRequestDto) {
        var category= this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        //Clase para actualizar solo los campos que hay que actualizar.
        this.bookCategoryMapper.patchFromDto(category, bookCategoryRequestDto);
        this.bookCategoryRepository.save(category);
    }

    @Override
    @Transactional
    public void putBookCategory(Long id, BookCategoryRequestDto bookUpdateDto) {
        var category = this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        this.bookCategoryRepository.save(this.bookCategoryMapper.putFromDto(category,bookUpdateDto));

    }

    @Override
    @Transactional(readOnly=true)
    public BookCategoryResponseDto findById(Long id) {
        var category = this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        return this.bookCategoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly=true)
    public List<BookCategoryBooksResponseDto> findByBookCategoryDescription(String description) {
        var categories = this.bookCategoryRepository.findByDescriptionContainingIgnoreCase(description);

        return this.bookCategoryMapper.toDtoListWithBooks(categories);

    }

    @Override
    @Transactional(readOnly=true)
    public BookCategoryBooksResponseDto findCategoryByIdAndBooks(Long id) {
        var category = this.bookCategoryRepository.findById(id)
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "+id+" no existe"));
        return this.bookCategoryMapper.toDtoWithBooks(category);
    }
}
