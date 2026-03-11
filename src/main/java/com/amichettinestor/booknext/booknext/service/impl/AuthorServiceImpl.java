package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Author;
import com.amichettinestor.booknext.booknext.exception.AuthorNotFoundException;
import com.amichettinestor.booknext.booknext.exception.LocationNotFound;
import com.amichettinestor.booknext.booknext.repository.AuthorRepository;
import com.amichettinestor.booknext.booknext.repository.LocationRepository;
import com.amichettinestor.booknext.booknext.service.AuthorService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly=true)
    public List<AuthorResponseDto> findAll() {

        var authors =this.authorRepository.findAll();
        return getAuthorResponseDtos(authors);
    }

    @Override
    @Transactional
    public void save(AuthorRequestDto authorRequestDto) {
        var location = this.locationRepository.findByNameAndCountryName(
                authorRequestDto.getLocation().getName(),
                authorRequestDto.getLocation().getCountryName())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad "+
                        authorRequestDto.getLocation().getName()
                        +" del país "+authorRequestDto.getLocation().getCountryName()));

        var author = new Author();
        author.setName(authorRequestDto.getName());
        author.setLastName(authorRequestDto.getLastName());
        author.setLocation(location);

        this.authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var author = this.authorRepository.findById(id)
                .orElseThrow(()->new AuthorNotFoundException("El autor con id: "+id+" no existe"));

        author.getBooks().forEach(author::removeBook);

        this.authorRepository.delete(author);
    }

    @Override
    @Transactional
    public void patchAuthor(Long id, AuthorUpdateDto authorUpdateDto) {
        var author = this.authorRepository.findById(id)
                .orElseThrow(()->new AuthorNotFoundException("El autor con id: "+id+" no existe"));

        var location = this.locationRepository.findByNameAndCountryName(
                        authorUpdateDto.getLocation().getName(),
                        authorUpdateDto.getLocation().getCountryName())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad "+
                        authorUpdateDto.getLocation().getName()
                        +" del país "+authorUpdateDto.getLocation().getCountryName()));

        author.setLocation(location);
        //Clase para actualizar solo los campos que hay que actualizar.
        PatchUtils.copyNonNullProperties(authorUpdateDto, author);

        this.authorRepository.save(author);
    }

    @Override
    @Transactional
    public void putAuthor(Long id, AuthorRequestDto authorRequestDto) {
        var author = this.authorRepository.findById(id)
                .orElseThrow(()->new AuthorNotFoundException("El autor con id: "+id+" no existe"));

        var location = this.locationRepository.findByNameAndCountryName(
                        authorRequestDto.getLocation().getName(),
                        authorRequestDto.getLocation().getCountryName())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad "
                        +authorRequestDto.getLocation().getName()+ " del país "
                        + authorRequestDto.getLocation().getCountryName()));

        author.setLastName(authorRequestDto.getLastName());
        author.setName(authorRequestDto.getLastName());
        author.setLocation(location);

        this.authorRepository.save(author);
    }

    @Override
    @Transactional(readOnly=true)
    public List<AuthorResponseDto> search(String lastname, String country) {

        List<Author> authors;

        if (lastname != null && country != null) {
            authors = authorRepository.findByLastNameAndCountry(lastname, country);
        }
        else if (lastname != null) {
            authors = authorRepository
                    .findByLastNameContainingIgnoreCase(lastname);
        }
        else if (country != null) {
            authors = authorRepository.findByLocation_Country_NameContainingIgnoreCase(country);
        }
        else {
            authors = authorRepository.findAll();
        }

        return getAuthorResponseDtos(authors);
    }

    private List<AuthorResponseDto> getAuthorResponseDtos(List<Author> authors) {
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

    @Override
    @Transactional(readOnly=true)
    public AuthorResponseDto findById(Long id) {
        var author = this.authorRepository.findById(id)
                .orElseThrow(()->new AuthorNotFoundException("El autor con id: "+id+" no existe"));

        var location = this.locationRepository.findById(author.getLocation().getId())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad con id:"
                        +author.getLocation().getId()));

        AuthorResponseDto authorResponseDto = new AuthorResponseDto();
        authorResponseDto.setName(author.getName());
        authorResponseDto.setLastName(author.getLastName());
        authorResponseDto.setId(author.getId());
        authorResponseDto.setBooks(
                author.getBooks().stream()
                        .map(book -> BookSummaryDto.builder()
                                .isbn(book.getIsbn())
                                .title(book.getTitle())
                                .build())
                        .collect(Collectors.toSet()));
        authorResponseDto.setLocation(LocationRequestDto.builder()
                        .name(location.getName())
                        .countryName(location.getCountry().getName())
                .build());

        return authorResponseDto;
    }
}
