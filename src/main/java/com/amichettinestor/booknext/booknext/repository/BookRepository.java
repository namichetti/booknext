package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {


    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Book> searchByText(@Param("text") String text);

    List<Book> findByPriceBetween(BigDecimal min, BigDecimal max);
}
