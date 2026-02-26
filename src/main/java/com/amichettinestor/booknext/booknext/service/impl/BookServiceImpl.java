package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.exception.AuthorNotFoundException;
import com.amichettinestor.booknext.booknext.exception.BookNotFoundException;
import com.amichettinestor.booknext.booknext.exception.PublisherNotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final PublisherRepository publisherCategory;

    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> findAll() {
        var books= this.bookRepository.findAll();
        return getBookResponseDtos(books);
    }

    @Override
    @Transactional(readOnly=true)
    public BookResponseDto findById(Long id) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("No se encontró el libro con id "+id));

        return BookResponseDto.builder()
                .id(book.getId())
                .authors(book.getAuthors().stream()
                        .map(author -> author.getName()+ " "+
                                author.getLastName())
                        .collect(Collectors.toSet()))
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .editionNumber(book.getEditionNumber())
                .dimensions(book.getDimensions())
                .pageCount(book.getPageCount())
                .weight(book.getWeight())
                .stock(book.getStock())
                .price(book.getPrice())
                .build();
    }

    @Override
    //Con lo establecido en las configuraciones alcanzaría pero,
    // puede haber casos en que se llegue a este método por otro camino.
    //Por eso podemos poner un "doble candado".
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void save(BookRequestDto bookRequestDto) {
        var book= Book.builder()
                .isbn(bookRequestDto.getIsbn())
                .title(bookRequestDto.getTitle())
                .description(bookRequestDto.getDescription())
                .editionNumber(bookRequestDto.getEditionNumber())
                .dimensions(bookRequestDto.getDimensions())
                .pageCount(bookRequestDto.getPageCount())
                .weight(bookRequestDto.getWeight())
                .stock(bookRequestDto.getStock())
                .price(bookRequestDto.getPrice())
                .build();

        this.bookRepository.save(book);
    }


    @Override
    public void deleteById(Long id) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("El libro con id: "+id+" no existe"));
        this.bookRepository.deleteById(id);
    }

    @Override
    public void patchBook(Long id, BookUpdateDto bookUpdateDto) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("El libro con id: "+id+" no existe"));

        //Clase para actualizar solo los campos que hay que actualizar.
        PatchUtils.copyNonNullProperties(bookUpdateDto, book);

        this.bookRepository.save(book);
    }

    @Override
    public void putBook(Long id, BookUpdateDto bookUpdateDto) {
        var book= this.bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("El libro con id: "+id+" no existe"));

        //Cuando encuentre true, que termine
        boolean anyAuthorExists = bookUpdateDto.getAuthorRequestDtos().stream()
                .anyMatch(authorRequestDto ->
                        authorRepository.existsByLastName(authorRequestDto.getLastName())
                );


        if (!anyAuthorExists) {
            throw new AuthorNotFoundException("Uno o más autores no existen");
        }

        var bookCategoryExist = bookCategoryRepository.existsByDescription(bookUpdateDto.getBookCategory().getDescription());
        if (!bookCategoryExist) {
            throw new AuthorNotFoundException("La categoría no existe.");
        }

        var publisheryExist =publisherCategory.existsByName(bookUpdateDto.getPublisherRequest().getName());
        if (!publisheryExist) {
            throw new PublisherNotFoundException("La editorial no existe.");
        }

        book.setEditionNumber(bookUpdateDto.getEditionNumber());
        book.setDimensions(bookUpdateDto.getDimensions());
        book.setPageCount(bookUpdateDto.getPageCount());
        book.setWeight(bookUpdateDto.getWeight());
        book.setStock(bookUpdateDto.getStock());
        book.setPrice(bookUpdateDto.getPrice());
        book.setDescription(bookUpdateDto.getDescription());
        book.setTitle(bookUpdateDto.getTitle());

        this.bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> searchByText(String text) {
        var books= this.bookRepository.searchByText(text);
        return getBookResponseDtos(books);
    }


    @Override
    @Transactional(readOnly=true)
    public List<BookResponseDto> searchByPriceRange(BigDecimal min, BigDecimal max) {
        var books= this.bookRepository.findByPriceBetween(min,max);
        return getBookResponseDtos(books);
    }

    private List<BookResponseDto> getBookResponseDtos(List<Book> books) {
        return books.stream()
                .map(book -> BookResponseDto.builder()
                        .id(book.getId())
                        .authors(book.getAuthors().stream()
                                .map(author -> author.getName()+ " "+
                                        author.getLastName())
                                .collect(Collectors.toSet()))
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
                                .map(author -> author.getName()+" "+author.getLastName())
                                .collect(Collectors.toSet())).build())
                .toList();
    }
}
