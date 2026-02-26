package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    boolean existsByName(String name);

    Optional<Publisher> findByName(String name);

    List<Publisher> findByNameContainingIgnoreCase(String name);
}
