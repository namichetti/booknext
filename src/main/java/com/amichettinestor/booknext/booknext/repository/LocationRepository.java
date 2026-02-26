package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByNameAndCountryName(String locationName, String countryName);

    Optional<Location> findByName(String name);
}
