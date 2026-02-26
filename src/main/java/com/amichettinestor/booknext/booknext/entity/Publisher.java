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
@Table(name="publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;

    //CascadeType.REMOVE porque si borro publisher se van a borrar todos los libros.

    // Elimina de la base de datos los Book que quedan sin Publisher
    // sin tener que llamar al repositorio
    @OneToMany(mappedBy = "publisher",cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude // evita que books se incluya en toString()
    @Builder.Default
    private Set<Book> books=new HashSet<>();

    @Builder
    public Publisher(String name) {
        this.name = name;
    }

    public void addBook(Book book) {
        this.books.add(book);
        book.setPublisher(this);
    }

}
