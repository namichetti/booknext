package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    List<BookCategory> findByDescriptionContainingIgnoreCase(String description);

}
