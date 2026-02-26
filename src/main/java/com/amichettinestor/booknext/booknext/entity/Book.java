package com.amichettinestor.booknext.booknext.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name="books")
public class Book {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    //solo incluye este atributo en hascode y equeals. Con el id es suficiente
    @EqualsAndHashCode.Include
    private Long id;

    private String isbn;
    private String title;
    private String description;
    private Integer editionNumber;

    @CreationTimestamp
    private LocalDateTime publicationDate;

    private String dimensions;
    private Integer pageCount;
    private Double weight;

    private Integer stock;
    private BigDecimal price;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private BookCategory bookCategory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    @Builder
    public Book(String isbn, String title, String description, Integer editionNumber,
                String dimensions, Integer pageCount, Double weight,
                Integer stock, BigDecimal price) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.editionNumber = editionNumber;
        this.dimensions = dimensions;
        this.pageCount = pageCount;
        this.weight = weight;
        this.stock = stock;
        this.price = price;
    }

    void setBookCategory(BookCategory category) {
        this.bookCategory = category;
    }

    public void addAuthor(Author author){
        this.authors.add(author);
        author.getBooks().add(this);
    }
}
