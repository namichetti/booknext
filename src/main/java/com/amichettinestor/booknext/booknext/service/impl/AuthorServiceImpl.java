package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Author;
import com.amichettinestor.booknext.booknext.exception.AuthorNotFoundException;
import com.amichettinestor.booknext.booknext.exception.LocationNotFound;
import com.amichettinestor.booknext.booknext.mapper.AuthorMapper;
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
    private final AuthorMapper authorMapper;

    @Override
    @Transactional(readOnly=true)
    public List<AuthorResponseDto> findAll() {

        var authors =this.authorRepository.findAll();
        return this.authorMapper.toDtoList(authors);
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

        //Clase para actualizar solo los campos que hay que actualizar.
        this.authorMapper.patchFromDto(authorUpdateDto,author,location);

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

        this.authorRepository.save(this.authorMapper.toEntity(authorRequestDto,author,location));
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

        return this.authorMapper.toDtoList(authors);
    }

    @Override
    @Transactional(readOnly=true)
    public AuthorResponseDto findById(Long id) {
        var author = this.authorRepository.findById(id)
                .orElseThrow(()->new AuthorNotFoundException("El autor con id: "+id+" no existe"));

        var location = this.locationRepository.findById(author.getLocation().getId())
                .orElseThrow(()->new LocationNotFound("No se encontró la localidad con id:"
                        +author.getLocation().getId()));

        return this.authorMapper.toDto(author,location);
    }
}
