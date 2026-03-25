package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.exception.AuthorNotFoundException;
import com.amichettinestor.booknext.booknext.exception.BookCategoryNotFoundException;
import com.amichettinestor.booknext.booknext.exception.BookNotFoundException;
import com.amichettinestor.booknext.booknext.exception.PublisherNotFoundException;
import com.amichettinestor.booknext.booknext.mapper.BookMapper;
import com.amichettinestor.booknext.booknext.repository.AuthorRepository;
import com.amichettinestor.booknext.booknext.repository.BookCategoryRepository;
import com.amichettinestor.booknext.booknext.repository.BookRepository;
import com.amichettinestor.booknext.booknext.repository.PublisherRepository;
import com.amichettinestor.booknext.booknext.service.BookService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final PublisherRepository publisherCategory;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> findAll() {
        var books= this.bookRepository.findAll();
        return this.bookMapper.toDtoList(books);
    }

    @Override
    @Transactional(readOnly=true)
    public BookResponseDto findById(Long id) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("No se encontró el libro con id "+id));
        return this.bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public void save(BookRequestDto bookRequestDto) {

        var category=  this.bookCategoryRepository.findById(bookRequestDto.getBookCategoryId())
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría con id "
                        +bookRequestDto.getBookCategoryId() +" no existe."));

        var publisher=this.publisherCategory.findById(bookRequestDto.getPublisherId())
                .orElseThrow(()->new PublisherNotFoundException("La editorial con id "
                        +bookRequestDto.getPublisherId() +" no existe."));

        var authorIds = bookRequestDto.getAuthorId();
        var authors = new HashSet<>(authorRepository.findAllById(authorIds));

        this.bookRepository.save(this.bookMapper.toEntity(bookRequestDto,publisher,category,authors));
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        if(this.bookRepository.existsById(id)) {
            throw new BookNotFoundException("El libro con id: "+id+" no existe");
        }
        this.bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void patchBook(Long id, BookUpdateDto bookUpdateDto) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("El libro con id: "+id+" no existe"));

        var authorIds = bookUpdateDto.getAuthorId();

        var authors = new HashSet<>(authorRepository.findAllById(authorIds));

        if (authors.size() != authorIds.size()) {
            throw new AuthorNotFoundException("Uno o más autores no existen");
        }

        var bookCategory = bookCategoryRepository.findById(bookUpdateDto.getBookCategoryId())
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría no existe."));

        var publisher =publisherCategory.findById(bookUpdateDto.getPublisherId())
                .orElseThrow(()-> new PublisherNotFoundException("La editorial no existe."));
        this.bookMapper.patchFromDto(book,bookUpdateDto,publisher,bookCategory,authors);
        this.bookRepository.save(book);
    }

    @Override
    @Transactional
    public void putBook(Long id, BookUpdateDto bookUpdateDto) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("El libro con id: "+id+" no existe"));

        var authorIds = bookUpdateDto.getAuthorId();

        var authors = new HashSet<>(authorRepository.findAllById(authorIds));

        if (authors.size() != authorIds.size()) {
            throw new AuthorNotFoundException("Uno o más autores no existen");
        }

        var bookCategory = bookCategoryRepository.findById(bookUpdateDto.getBookCategoryId())
                .orElseThrow(()->new BookCategoryNotFoundException("La categoría no existe."));

        var publisher =publisherCategory.findById(bookUpdateDto.getPublisherId())
                .orElseThrow(()-> new PublisherNotFoundException("La editorial no existe."));

        this.bookRepository.save(this.bookMapper
                .putFromDto(book,bookUpdateDto,publisher,bookCategory,authors));
    }

    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> searchByText(String text) {
        var books= this.bookRepository.searchByText(text);
        return this.bookMapper.toDtoList(books);
    }


    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> searchByPriceRange(BigDecimal min, BigDecimal max) {
        var books= this.bookRepository.findByPriceBetween(min,max);
        return this.bookMapper.toDtoList(books);
    }

}
