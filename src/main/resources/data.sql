-- Desactivar temporalmente cheques de claves foráneas
SET FOREIGN_KEY_CHECKS=0;

START TRANSACTION;
-- 🔹 BORRAR DATOS EXISTENTES Y RESET AUTO_INCREMENT
DELETE FROM books_authors;
DELETE FROM books;
DELETE FROM people;
DELETE FROM locations;
DELETE FROM countries;
DELETE FROM book_categories;
DELETE FROM publishers;

ALTER TABLE books_authors AUTO_INCREMENT = 1;
ALTER TABLE books AUTO_INCREMENT = 1;
ALTER TABLE people AUTO_INCREMENT = 1;
ALTER TABLE locations AUTO_INCREMENT = 1;
ALTER TABLE countries AUTO_INCREMENT = 1;
ALTER TABLE book_categories AUTO_INCREMENT = 1;
ALTER TABLE publishers AUTO_INCREMENT = 1;

-- 🔹 INSERT DE PAÍSES
INSERT IGNORE INTO countries (name) VALUES
('Argentina'),('Brasil'),('Estados Unidos'),('Canadá'),('México'),
('España'),('Francia'),('Alemania'),('Italia'),('Reino Unido'),
('China'),('Japón'),('Rusia'),('Colombia');

-- 🔹 LOCACIONES DE CIUDADES GENERALES
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Buenos Aires', id FROM countries WHERE name='Argentina';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Córdoba', id FROM countries WHERE name='Argentina';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Rosario', id FROM countries WHERE name='Argentina';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'São Paulo', id FROM countries WHERE name='Brasil';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Río de Janeiro', id FROM countries WHERE name='Brasil';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Brasília', id FROM countries WHERE name='Brasil';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Nueva York', id FROM countries WHERE name='Estados Unidos';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Los Ángeles', id FROM countries WHERE name='Estados Unidos';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Chicago', id FROM countries WHERE name='Estados Unidos';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Ciudad de México', id FROM countries WHERE name='México';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Guadalajara', id FROM countries WHERE name='México';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Monterrey', id FROM countries WHERE name='México';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Madrid', id FROM countries WHERE name='España';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Barcelona', id FROM countries WHERE name='España';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Sevilla', id FROM countries WHERE name='España';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'París', id FROM countries WHERE name='Francia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Lyon', id FROM countries WHERE name='Francia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Marsella', id FROM countries WHERE name='Francia';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Berlín', id FROM countries WHERE name='Alemania';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Múnich', id FROM countries WHERE name='Alemania';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Hamburgo', id FROM countries WHERE name='Alemania';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Roma', id FROM countries WHERE name='Italia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Milán', id FROM countries WHERE name='Italia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Nápoles', id FROM countries WHERE name='Italia';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Londres', id FROM countries WHERE name='Reino Unido';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Manchester', id FROM countries WHERE name='Reino Unido';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Edimburgo', id FROM countries WHERE name='Reino Unido';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Cambridge', id FROM countries WHERE name='Reino Unido';


INSERT IGNORE INTO locations (name, country_id)
SELECT 'Pekín', id FROM countries WHERE name='China';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Shanghái', id FROM countries WHERE name='China';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Guangzhou', id FROM countries WHERE name='China';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Tokio', id FROM countries WHERE name='Japón';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Osaka', id FROM countries WHERE name='Japón';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Kioto', id FROM countries WHERE name='Japón';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Bogotá', id FROM countries WHERE name='Colombia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Cali', id FROM countries WHERE name='Colombia';

INSERT IGNORE INTO locations (name, country_id)
SELECT 'Moscú', id FROM countries WHERE name='Rusia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'San Petersburgo', id FROM countries WHERE name='Rusia';
INSERT IGNORE INTO locations (name, country_id)
SELECT 'Novosibirsk', id FROM countries WHERE name='Rusia';


-- 🔹 INSERT CATEGORÍAS
INSERT IGNORE INTO book_categories (description) VALUES
('Ficción'),('No Ficción'),('Ciencia'),('Historia'),('Filosofía'),
('Arte'),('Música'),('Tecnología'),('Psicología'),('Biografía'),
('Viajes'),('Religión'),('Poesía'),('Infantil'),('Juvenil');

-- 🔹 INSERT EDITORIALES
INSERT IGNORE INTO publishers (name) VALUES
('Penguin Random House'),('HarperCollins'),('Simon & Schuster'),('Hachette Livre'),
('Macmillan Publishers'),('Scholastic'),('Oxford University Press'),('Cambridge University Press'),
('Pearson'),('Wiley'),('Springer'),('Taylor & Francis'),('Grupo Planeta'),('Editorial Planeta'),('Santillana');

-- 🔹 INSERT AUTORES
INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Gabriel', 'García', id FROM locations WHERE name='Bogotá';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Isaac', 'Asimov', id FROM locations WHERE name='Nueva York';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'J.K.', 'Rowling', id FROM locations WHERE name='Cambridge';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Ernest', 'Hemingway', id FROM locations WHERE name='Chicago';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Paulo', 'Coelho', id FROM locations WHERE name='São Paulo';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Haruki', 'Murakami', id FROM locations WHERE name='Tokio';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Frida', 'Kahlo', id FROM locations WHERE name='Ciudad de México';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Victor', 'Hugo', id FROM locations WHERE name='París';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'George', 'Orwell', id FROM locations WHERE name='Londres';

INSERT IGNORE INTO people (person_type, name, last_name, location_id)
SELECT 'AUTHOR', 'Leo', 'Tolstói', id FROM locations WHERE name='Moscú';


-- 🔹 INSERT LIBROS
INSERT IGNORE INTO books
(description, dimensions, edition_number, isbn, page_count, price, publication_date, stock, title, weight, category_id, publisher_id)
VALUES
('Una novela épica sobre la vida en Buenos Aires', '15x23 cm', 1, '978-987-12345-01-0', 350, 25.99, '2023-06-15', 10, 'Sueños Porteños', 0.5,
 (SELECT id FROM book_categories WHERE description='Ficción'),
 (SELECT id FROM publishers WHERE name='Penguin Random House'));

INSERT IGNORE INTO books
(description, dimensions, edition_number, isbn, page_count, price, publication_date, stock, title, weight, category_id, publisher_id)
VALUES
('Ciencia ficción ambientada en Nueva York y sus alrededores', '16x24 cm', 2, '978-987-12345-02-7', 420, 30.50, '2022-09-10', 5, 'Horizontes de Asimov', 0.6,
 (SELECT id FROM book_categories WHERE description='Ciencia'),
 (SELECT id FROM publishers WHERE name='HarperCollins'));

INSERT IGNORE INTO books
(description, dimensions, edition_number, isbn, page_count, price, publication_date, stock, title, weight, category_id, publisher_id)
VALUES
('Una historia fantástica basada en Cambridge', '14x21 cm', 1, '978-987-12345-03-4', 320, 22.00, '2021-03-25', 8, 'Magia en la Biblioteca', 0.45,
 (SELECT id FROM book_categories WHERE description='Ficción'),
 (SELECT id FROM publishers WHERE name='Oxford University Press'));

INSERT IGNORE INTO books
(description, dimensions, edition_number, isbn, page_count, price, publication_date, stock, title, weight, category_id, publisher_id)
VALUES
('Crónica histórica de Moscú a través del tiempo', '17x25 cm', 1, '978-987-12345-04-1', 500, 35.00, '2020-11-05', 12, 'Relatos de Moscú', 0.7,
 (SELECT id FROM book_categories WHERE description='Historia'),
 (SELECT id FROM publishers WHERE name='Cambridge University Press'));

INSERT IGNORE INTO books
(description, dimensions, edition_number, isbn, page_count, price, publication_date, stock, title, weight, category_id, publisher_id)
VALUES
('Poesía de la vida urbana en São Paulo', '15x22 cm', 1, '978-987-12345-05-8', 200, 18.50, '2023-01-12', 15, 'Versos de la Cidade', 0.35,
 (SELECT id FROM book_categories WHERE description='Poesía'),
 (SELECT id FROM publishers WHERE name='Santillana'));

-- 🔹 RELACIÓN AUTORES-LIBROS
INSERT IGNORE INTO books_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN people a ON b.title='Sueños Porteños' AND a.last_name='García';

INSERT IGNORE INTO books_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN people a ON b.title='Horizontes de Asimov' AND a.last_name='Asimov';

INSERT IGNORE INTO books_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN people a ON b.title='Magia en la Biblioteca' AND a.last_name='Rowling';

INSERT IGNORE INTO books_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN people a ON b.title='Relatos de Moscú' AND a.last_name='Tolstói';

INSERT IGNORE INTO books_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN people a ON b.title='Versos de la Cidade' AND a.last_name='Coelho';

COMMIT;

-- Desactivar temporalmente cheques de claves foráneas
SET FOREIGN_KEY_CHECKS=1;
