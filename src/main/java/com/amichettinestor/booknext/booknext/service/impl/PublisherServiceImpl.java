package com.amichettinestor.booknext.booknext.service.impl;

import com.amichettinestor.booknext.booknext.dto.*;
import com.amichettinestor.booknext.booknext.entity.Publisher;
import com.amichettinestor.booknext.booknext.exception.PublisherAlreadyExistsException;
import com.amichettinestor.booknext.booknext.exception.PublisherNotFoundException;
import com.amichettinestor.booknext.booknext.repository.PublisherRepository;
import com.amichettinestor.booknext.booknext.service.PublisherService;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    @Override
    public void putPublisher(Long id, PublisherUpdateDto publisherUpdateDto) {
        var publisher = this.publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException("No se encontró la editorial con id: "+
                        id));

        publisher.setName(publisherUpdateDto.getName());

        this.publisherRepository.save(publisher);
    }

    @Override
    public void patchPublisher(Long id,PublisherUpdateDto publisherUpdateDto) {
        var publisher = this.publisherRepository.findById(id)
                .orElseThrow(() -> new PublisherNotFoundException("No se encontró la editorial con id: "+
                        id));

        PatchUtils.copyNonNullProperties(publisherUpdateDto, publisher);

        this.publisherRepository.save(publisher);
    }

    @Override
    @Transactional(readOnly=true)
    public List<PublisherResponseDto> search(String name) {
        var publishers = this.publisherRepository.findByNameContainingIgnoreCase(name);

        if (publishers==null || publishers.isEmpty()) {
            throw new PublisherNotFoundException("No se encontraron editoriales");
        }

        return publishers.stream()
                .map(publisher -> PublisherResponseDto.builder()
                        .name(publisher.getName())
                        .books(publisher.getBooks().stream()
                                .map(book -> BookSummaryDto.builder()
                                        .isbn(book.getIsbn())
                                        .title(book.getTitle())
                                        .build()
                                )
                                .collect(Collectors.toSet()))
                        .build()
                )
                .toList();
    }

    @Override
    public void save(PublisherRequestDto publisherRequestDto) {
        var publisheryExist =this.publisherRepository.existsByName(publisherRequestDto.getName());

        if (publisheryExist) {
            throw new PublisherAlreadyExistsException("La editorial ya existe.");
        }

        var publisher = Publisher.builder()
                .name(publisherRequestDto.getName())
                .build();

        publisherRepository.save(publisher);
    }

    @Override
    public void deleteById(Long id) {
        publisherRepository.findById(id)
                .orElseThrow(()->new PublisherNotFoundException("No se encontró la editorial con id "+id));

        publisherRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly=true)
    public PublisherResponseDto findById(Long id) {
        var publisher = publisherRepository.findById(id)
                .orElseThrow(()->new PublisherNotFoundException("No se encontró la editorial con id "+id));

        return PublisherResponseDto.builder()
                .name(publisher.getName())
                .books(publisher.getBooks().stream()
                        .map(book -> BookSummaryDto.builder()
                        .isbn(book.getIsbn())
                        .title(book.getTitle())
                        .build()
                )
                .collect(Collectors.toSet())).build();
    }

    @Override
    @Transactional(readOnly=true)
    public List<PublisherResponseDto> findAll() {
        var publishers=this.publisherRepository.findAll();

        return publishers.stream()
                .map(publisher -> PublisherResponseDto.builder()
                        .name(publisher.getName())
                        .books(publisher.getBooks().stream()
                                        .map(book -> BookSummaryDto.builder()
                                                .isbn(book.getIsbn())
                                                .title(book.getTitle())
                                                .build()
                                        )
                                        .collect(Collectors.toSet()))
                        .build()
                )
                .toList();
    }
}
