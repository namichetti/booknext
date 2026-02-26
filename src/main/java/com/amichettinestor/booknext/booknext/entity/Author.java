package com.amichettinestor.booknext.booknext.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//La columna person_type tendrá "AUTHOR"
@DiscriminatorValue("AUTHOR")
public class Author extends Person{

    @Setter(AccessLevel.NONE) // No se puede setear desde Builder ni de afuera.
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();


    public void addBook(Book book){
        books.add(book);
        book.getAuthors().add(this);
    }

}
