package com.amichettinestor.booknext.booknext;

import com.amichettinestor.booknext.booknext.entity.*;
import com.amichettinestor.booknext.booknext.enums.Role;
import com.amichettinestor.booknext.booknext.enums.UserStatus;
import com.amichettinestor.booknext.booknext.exception.LocationCountryNotFoundException;
import com.amichettinestor.booknext.booknext.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class BooknextApplication implements CommandLineRunner {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CountryRepository countryRepository;

    public static void main(String[] args) {
		SpringApplication.run(BooknextApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
       //Insertamos un admin y un manager si es que no existe en la BD.
        if (this.userRepository.findByUsername("admin").isEmpty() &&
                this.userRepository.findByUsername("manager").isEmpty()) {
            var admin = new User();
            admin.setUsername("admin");
            admin.setPassword(bCryptPasswordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setName("Nestor");
            admin.setLastName("Amichetti");
            admin.setEmail("nestor@gmail.com");
            admin.setStatus(UserStatus.ACTIVE);
            admin.setAddress("Calle 123");

            var location=this.locationRepository.findByNameAndCountryName("Rosario","Argentina")
                            .orElseThrow(()->new LocationCountryNotFoundException("Locación no encontrada"));

            admin.setLocation(location);

            var manager = new User();
            manager.setUsername("manager");
            manager.setPassword(bCryptPasswordEncoder.encode("manager"));
            manager.setRole(Role.MANAGER);
            manager.setName("manager");
            manager.setLastName("manager");
            manager.setEmail("manager@gmail.com");
            manager.setStatus(UserStatus.ACTIVE);
            manager.setAddress("Calle 777");
            manager.setLocation(location);

            userRepository.saveAll(List.of(admin,manager));
        }

    }
}
