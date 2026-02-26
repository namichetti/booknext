package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {

    boolean existsByLastName(String lastName);

    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    List<Author> findByLocation_Country_NameContainingIgnoreCase(String countryName);


    @Query("""
        SELECT a FROM Author a
        WHERE LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))
        AND LOWER(a.location.country.name) LIKE LOWER(CONCAT('%', :country, '%'))
        """)
    List<Author> findByLastNameAndCountry(@Param("lastName") String lastName,
                                          @Param("country") String country);



}
