package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.PublisherRequestDto;
import com.amichettinestor.booknext.booknext.dto.PublisherResponseDto;
import com.amichettinestor.booknext.booknext.dto.PublisherUpdateDto;

import java.util.List;

public interface PublisherService {
    void putPublisher(Long id,PublisherUpdateDto publisherUpdateDto);

    void patchPublisher(Long id,PublisherUpdateDto publisherUpdateDto);

    List<PublisherResponseDto> search(String name);

    void save(PublisherRequestDto publisherRequestDto);

    void deleteById(Long id);

    PublisherResponseDto findById(Long id);

    List<PublisherResponseDto> findAll();
}
