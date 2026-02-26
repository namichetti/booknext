package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.AuthorRequestDto;
import com.amichettinestor.booknext.booknext.dto.AuthorResponseDto;
import com.amichettinestor.booknext.booknext.dto.AuthorUpdateDto;

import java.util.List;

public interface AuthorService {

    List<AuthorResponseDto> findAll();

    void save(AuthorRequestDto authorRequestDto);

    void deleteById(Long id);

    void patchAuthor(Long id, AuthorUpdateDto authorUpdateDto);

    void putAuthor(Long id, AuthorUpdateDto authorUpdateDto);

    List<AuthorResponseDto> search(String lastname, String country);

    AuthorResponseDto findById(Long id);
}
