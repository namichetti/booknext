package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.BookRequestDto;
import com.amichettinestor.booknext.booknext.dto.BookResponseDto;
import com.amichettinestor.booknext.booknext.dto.BookUpdateDto;
import com.amichettinestor.booknext.booknext.entity.Author;
import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.entity.BookCategory;
import com.amichettinestor.booknext.booknext.entity.Publisher;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookResponseDto toDto(Book book){
        return BookResponseDto.builder()
                .id(book.getId())
                .authors(book.getAuthors().stream()
                        .map(author -> author.getName()+ " "+
                                author.getLastName())
                        .collect(Collectors.toSet()))
                .isbn(book.getIsbn())
                .publisher(book.getPublisher().getName())
                .bookCategory(book.getBookCategory().getDescription())
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

    public Book toEntity(BookRequestDto bookRequestDto,
                         Publisher publisher,
                         BookCategory category,
                         HashSet<Author> authors){
        return Book.builder()
                .isbn(bookRequestDto.getIsbn())
                .title(bookRequestDto.getTitle())
                .description(bookRequestDto.getDescription())
                .editionNumber(bookRequestDto.getEditionNumber())
                .dimensions(bookRequestDto.getDimensions())
                .pageCount(bookRequestDto.getPageCount())
                .weight(bookRequestDto.getWeight())
                .stock(bookRequestDto.getStock())
                .price(bookRequestDto.getPrice())
                .publisher(publisher)
                .bookCategory(category)
                .authors(authors)
                .build();
    }
    public void patchFromDto(Book book,
                             BookUpdateDto bookUpdateDto,
                             Publisher publisher,
                             BookCategory bookCategory,
                             HashSet<Author> authors){

        book.setPublisher(publisher);
        book.setAuthors(authors);
        book.setBookCategory(bookCategory);
        PatchUtils.copyNonNullProperties(bookUpdateDto, book);
    }

    public Book putFromDto(Book book,
                           BookUpdateDto bookUpdateDto,
                           Publisher publisher,
                           BookCategory bookCategory,
                           HashSet<Author> authors) {
        book.setEditionNumber(bookUpdateDto.getEditionNumber());
        book.setDimensions(bookUpdateDto.getDimensions());
        book.setPageCount(bookUpdateDto.getPageCount());
        book.setWeight(bookUpdateDto.getWeight());
        book.setStock(bookUpdateDto.getStock());
        book.setPrice(bookUpdateDto.getPrice());
        book.setDescription(bookUpdateDto.getDescription());
        book.setTitle(bookUpdateDto.getTitle());
        book.setPublisher(publisher);
        book.setAuthors(authors);
        book.setBookCategory(bookCategory);
        return book;
    }

    public List<BookResponseDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(book -> BookResponseDto.builder()
                        .id(book.getId())
                        .authors(book.getAuthors().stream()
                                .map(author -> author.getName()+ " "+
                                        author.getLastName())
                                .collect(Collectors.toSet()))
                        .isbn(book.getIsbn())
                        .publisher(book.getPublisher().getName())
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
