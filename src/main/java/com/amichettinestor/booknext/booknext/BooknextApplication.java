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

        /*
        //Insertamos libros de muestra.

        var fiction = BookCategory.builder()
                .description("Fiction")
                .build();

        var classic = BookCategory.builder()
                .description("Classic Literature")
                .build();

        var publisher1 = Publisher.builder()
                .name("Editorial 1")
                .build();

        var publisher2 = Publisher.builder()
                .name("Editorial 2")
                .build();

        Author author1 = new Author();
        author1.setName("Antoine");
        author1.setLastName("de Saint-Exupéry");

        Author author2 = new Author();
        author2.setName("Harper");
        author2.setLastName("Lee");

        Author author3 = new Author();
        author3.setName("Gabriel");
        author3.setLastName("García Márquez");

        Author author4 = new Author();
        author4.setName("Dan");
        author4.setLastName("Brown");

        Author author5 = new Author();
        author5.setName("George");
        author5.setLastName("Orwell");


        Country argentina = Country.builder()
                .name("Argentina")
                .build();

        Country france = Country.builder()
                .name("France")
                .build();

        Country usa = Country.builder()
                .name("United States")
                .build();

        Country uk = Country.builder()
                .name("United Kingdom")
                .build();

        Country colombia = Country.builder()
                .name("Colombia")
                .build();

        Location buenosAires = Location.builder()
                .name("Buenos Aires")
                .build();

        Location paris = Location.builder()
                .name("Paris")
                .build();

        Location newYork = Location.builder()
                .name("New York")
                .build();

        Location london = Location.builder()
                .name("London")
                .build();

        Location araca = Location.builder()
                .name("Aracataca")
                .build();

        argentina.addLocation(buenosAires);
        france.addLocation(paris);
        usa.addLocation(newYork);
        uk.addLocation(london);
        colombia.addLocation(araca);

        paris.addPerson(author1);
        newYork.addPerson(author2);
        araca.addPerson(author3);
        london.addPerson(author4);
        london.addPerson(author5);


        var book1 = Book.builder()
                .isbn("978-3-16-148410-0")
                .title("El Principito")
                .description("Un clásico de la literatura infantil y filosófica")
                .editionNumber(1)
                .dimensions("20x13 cm")
                .pageCount(96)
                .weight(0.25)
                .stock(50)
                .price(new BigDecimal("100"))
                .build();

        var book2 = Book.builder()
                .isbn("978-0-06-112008-4")
                .title("To Kill a Mockingbird")
                .description("Novela sobre racismo y justicia en Estados Unidos")
                .editionNumber(2)
                .dimensions("21x14 cm")
                .pageCount(336)
                .weight(0.45)
                .stock(30)
                .price(new BigDecimal("100.50"))
                .build();

        var book3 = Book.builder()
                .isbn("978-84-376-0494-7")
                .title("Cien años de soledad")
                .description("Obra maestra de Gabriel García Márquez")
                .editionNumber(1)
                .dimensions("22x15 cm")
                .pageCount(417)
                .weight(0.55)
                .stock(40)
                .price(new BigDecimal("200"))
                .build();

        var book4 = Book.builder()
                .isbn("978-0-7432-7356-5")
                .title("Angels & Demons")
                .description("Thriller de misterio y conspiración")
                .editionNumber(3)
                .dimensions("23x16 cm")
                .pageCount(616)
                .weight(0.75)
                .stock(25)
                .price(new BigDecimal("1000"))
                .build();

        var book5 = Book.builder()
                .isbn("978-0-452-28423-4")
                .title("1984")
                .description("Distopía sobre vigilancia y totalitarismo")
                .editionNumber(1)
                .dimensions("19x12 cm")
                .pageCount(328)
                .weight(0.40)
                .stock(60)
                .price(new BigDecimal("150"))
                .build();

        classic.addBook(book1);
        classic.addBook(book2);
        classic.addBook(book3);

        fiction.addBook(book4);
        fiction.addBook(book5);

        publisher1.addBook(book1);
        publisher1.addBook(book2);
        publisher1.addBook(book3);
        publisher2.addBook(book4);
        publisher2.addBook(book5);

        book1.addAuthor(author1);
        book2.addAuthor(author2);
        book3.addAuthor(author3);
        book4.addAuthor(author4);
        book5.addAuthor(author5);


        List<Publisher> publishers = Stream.of(publisher1,publisher2)
                .filter(publisher -> !publisherRepository.existsByName(publisher.getName()))
                .toList();

        List<BookCategory> categories = Stream.of(fiction, classic)
                .filter(category ->
                        !bookCategoryRepository.existsByDescription(category.getDescription()))
                .toList();

        List<Author> authors = Stream.of(author1,author2,author3,author4,author5)
                        .filter(author ->
                                !this.authorRepository.existsByLastName(author.getLastName()))
                .toList();

        List<Country> countries = Stream.of(argentina,france,usa,uk,colombia)
                .filter(country ->
                        !this.countryRepository.existsByName(country.getName()))
                .toList();

        List<Location> locations=Stream.of(buenosAires, paris, newYork,london,araca)
                .filter(location ->
                        !this.locationRepository.existsByName(location.getName()))
                .toList();

        bookCategoryRepository.saveAll(categories);

        this.publisherRepository.saveAll(publishers);

        this.countryRepository.saveAll(countries);
        this.locationRepository.saveAll(locations);

        this.authorRepository.saveAll(authors);



        List<Book> books = Stream.of(book1, book2, book3, book4, book5)
                .filter(book -> !bookRepository.existsByIsbn(book.getIsbn()))
                .toList();

        bookRepository.saveAll(books);

        */
    }
}
