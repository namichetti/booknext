package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.AuthorRequestDto;
import com.amichettinestor.booknext.booknext.dto.AuthorResponseDto;
import com.amichettinestor.booknext.booknext.dto.AuthorUpdateDto;
import com.amichettinestor.booknext.booknext.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {

    private final AuthorService authorService;


    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> getAllAuthor() {
        var authors = this.authorService.findAll();
        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(authors);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getAuthor(@PathVariable Long id) {
        var author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }


    @PostMapping
    public ResponseEntity<String> saveAuthor(@Valid @RequestBody AuthorRequestDto authorRequestDto){
        this.authorService.save(authorRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("El autor se ha guardado correctamente.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long id) {
        this.authorService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<String> patchAuthor(@PathVariable Long id,
                                              @Valid @RequestBody AuthorUpdateDto authorUpdateDto) {
        this.authorService.patchAuthor(id, authorUpdateDto);
        return ResponseEntity.ok("El autor se actualizó correctamente.");
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> putAuthor(@PathVariable Long id,
                                            @Valid @RequestBody AuthorRequestDto authorRequestDto) {
        this.authorService.putAuthor(id, authorRequestDto);
        return ResponseEntity.ok("El autor se actualizó correctamente.");
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchAuthors(
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String country) {
        var authors = authorService.search(lastname, country);
        if (authors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay autores con los criterios dados");
        }
        return ResponseEntity.ok(authors);
    }

}
