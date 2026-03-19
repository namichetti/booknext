package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Book;
import com.amichettinestor.booknext.booknext.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
           SELECT oi.book
           FROM OrderItem oi
           WHERE oi.book.isbn = :isbn
           """)
    Optional<Book> findBookByIsbn(String isbn);


}
