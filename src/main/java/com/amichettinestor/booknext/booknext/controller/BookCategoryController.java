package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.BookCategoryBooksResponseDto;
import com.amichettinestor.booknext.booknext.dto.BookCategoryRequestDto;
import com.amichettinestor.booknext.booknext.dto.BookCategoryResponseDto;
import com.amichettinestor.booknext.booknext.service.BookCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bookcategory")
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @GetMapping
    public ResponseEntity<List<BookCategoryResponseDto>> getAllBookCategories() {
        var books= this.bookCategoryService.findAll();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<BookCategoryBooksResponseDto> getAllBookCategoriesAndBooks(
            @PathVariable Long id
    ) {
        var book= this.bookCategoryService.findCategoryByIdAndBooks(id);
        return ResponseEntity.ok(book);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookCategoryResponseDto> getBookCategory(@PathVariable Long id) {
        var book= this.bookCategoryService.findById(id);
        return ResponseEntity.ok(book);
    }


    @PostMapping
    public ResponseEntity<String> saveBookCategory(@Valid @RequestBody BookCategoryRequestDto bookCategoryRequestDto){
        this.bookCategoryService.save(bookCategoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("La categoría se ha guardado correctamente.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        this.bookCategoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<String> patchBook(@PathVariable Long id,
                                            @Valid @RequestBody BookCategoryRequestDto bookCategoryRequestDto) {
        this.bookCategoryService.patchBookCategory(id,bookCategoryRequestDto);
        return ResponseEntity.ok("La categoría se actualizó correctamente.");
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> putBook(@PathVariable Long id,
                                          @Valid @RequestBody BookCategoryRequestDto bookCategoryRequestDto) {
        this.bookCategoryService.putBookCategory(id,bookCategoryRequestDto);
        return ResponseEntity.ok("La categoría se actualizó correctamente.");
    }



    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam String description) {
        var books = bookCategoryService.findByBookCategoryDescription(description);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay categorías con la descripción dada");
        }
        return ResponseEntity.ok(books);
    }

}
