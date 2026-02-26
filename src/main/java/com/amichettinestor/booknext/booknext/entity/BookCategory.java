package com.amichettinestor.booknext.booknext.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Ignorá todos los campos excepto los que tienen @EqualsAndHashCode.Include
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="book_categories")
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //solo incluye este atributo en hascode y equeals. Con el id es suficiente
    @EqualsAndHashCode.Include
    private Long id;
    private String description;

    //MappedBy indica que la relación está definida por el atributo
    //bookCategory en la entidad Book
    @OneToMany(mappedBy = "bookCategory")
    @ToString.Exclude // evita que books se incluya en toString()
    @Builder.Default
    private Set<Book> books = new HashSet<>();

    @Builder
    public BookCategory(String description) {
        this.description = description;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setBookCategory(this);
    }
}

