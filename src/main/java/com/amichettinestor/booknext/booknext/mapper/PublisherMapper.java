package com.amichettinestor.booknext.booknext.mapper;

import com.amichettinestor.booknext.booknext.dto.BookSummaryDto;
import com.amichettinestor.booknext.booknext.dto.PublisherRequestDto;
import com.amichettinestor.booknext.booknext.dto.PublisherResponseDto;
import com.amichettinestor.booknext.booknext.dto.PublisherUpdateDto;
import com.amichettinestor.booknext.booknext.entity.Publisher;
import com.amichettinestor.booknext.booknext.util.PatchUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PublisherMapper {

    public PublisherResponseDto toDto(Publisher publisher){
        return PublisherResponseDto.builder()
                .name(publisher.getName())
                .id(publisher.getId())
                .books(publisher.getBooks().stream()
                        .map(book -> BookSummaryDto.builder()
                                .id(book.getId())
                                .isbn(book.getIsbn())
                                .title(book.getTitle())
                                .build()
                        )
                        .collect(Collectors.toSet())).build();
    }

    public Publisher toEntity(PublisherRequestDto publisherRequestDto){
        return Publisher.builder()
                .name(publisherRequestDto.getName())
                .build();
    }


    public void patchFromDto(Publisher publisher, PublisherUpdateDto publisherUpdateDto){
        PatchUtils.copyNonNullProperties(publisherUpdateDto, publisher);
    }

    public Publisher putFromDto(Publisher publisher, PublisherUpdateDto publisherUpdateDto){
        publisher.setName(publisherUpdateDto.getName());
        return publisher;
    }

    public List<PublisherResponseDto> toDtoList(List<Publisher> publishers) {
        return publishers.stream()
                .map(publisher -> PublisherResponseDto.builder()
                        .name(publisher.getName())
                        .id(publisher.getId())
                        .books(publisher.getBooks().stream()
                                .map(book -> BookSummaryDto.builder()
                                        .id(book.getId())
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
