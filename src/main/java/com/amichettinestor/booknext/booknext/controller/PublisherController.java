package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.PublisherRequestDto;
import com.amichettinestor.booknext.booknext.dto.PublisherResponseDto;
import com.amichettinestor.booknext.booknext.dto.PublisherUpdateDto;
import com.amichettinestor.booknext.booknext.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherResponseDto>> getAllPublishers() {
        var publishers = this.publisherService.findAll();
        if (publishers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponseDto> getPublisher(@PathVariable Long id) {
        var publisher = publisherService.findById(id);
        return ResponseEntity.ok(publisher);
    }

    @PostMapping
    public ResponseEntity<String> savePublisher(@Valid @RequestBody PublisherRequestDto publisherRequestDto){
        this.publisherService.save(publisherRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("La editorial se ha guardado correctamente.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long id) {
        this.publisherService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> patchPublisher(@PathVariable Long id,
                                                 @Valid @RequestBody PublisherUpdateDto publisherUpdateDto) {
        this.publisherService.patchPublisher(id,publisherUpdateDto);
        return ResponseEntity.ok("La editorial se actualizó correctamente.");
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> putPublisher(@PathVariable Long id,
                                               @Valid @RequestBody PublisherUpdateDto publisherUpdateDto) {
        this.publisherService.putPublisher(id,publisherUpdateDto);
        return ResponseEntity.ok("La editorial se actualizó correctamente.");
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchPublisher(@RequestParam String name) {
        var publishers = this.publisherService.search(name);
        if (publishers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay editoriales con los criterios dados");
        }
        return ResponseEntity.ok(publishers);
    }

}
