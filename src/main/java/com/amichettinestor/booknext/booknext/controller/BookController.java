package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.BookRequestDto;
import com.amichettinestor.booknext.booknext.dto.BookResponseDto;
import com.amichettinestor.booknext.booknext.dto.BookUpdateDto;
import com.amichettinestor.booknext.booknext.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/book")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        var books= this.bookService.findAll();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable Long id) {
        var book= this.bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<String> saveBook(@Valid @RequestBody BookRequestDto bookRequestDto){
        this.bookService.save(bookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("El libro se ha guardado correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        this.bookService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<String> patchBook(@PathVariable Long id,
                                            @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        this.bookService.patchBook(id,bookUpdateDto);
        return ResponseEntity.ok("El libro se actualizó correctamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> putBook(@PathVariable Long id,
                                          @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        this.bookService.putBook(id,bookUpdateDto);
        return ResponseEntity.ok("El libro se actualizó correctamente.");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam String text) {
        var books = bookService.searchByText(text);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay libros con la descripción dada");
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/price")
    public ResponseEntity<?> searchBooksByPrice(@RequestParam(required = true) BigDecimal min,
                                                @RequestParam(required = true) BigDecimal max) {
        var books = bookService.searchByPriceRange(min, max);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay libros en el rango de precios ingresos");
        }
        return ResponseEntity.ok(books);
    }

}
