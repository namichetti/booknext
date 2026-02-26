package com.amichettinestor.booknext.booknext.entity;

import jakarta.persistence.*;
import lombok.*;

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
@Table(name="locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //solo incluye este atributo en hascode y equeals. Con el id es suficiente
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    //MappedBy indica que la relación está definida por el atributo
    //bookCategory en la entidad Book
    @OneToMany(mappedBy = "location")
    @ToString.Exclude // evita que people se incluya en toString()
    @Builder.Default
    private Set<Person> people = new HashSet<>();

    //optional = false y  nullable = false me asegura que el lado N tenga mínimo 1 y no 0.

    //Este es el lado dueño y define la FK
    //optional = false. Indica que no es válida una referencia nula, es decir una BookCategory.
    //FetchType.LAZY La categoría no se carga automáticamente y se trae solo si se lo llama
    @ManyToOne(optional = false, fetch = FetchType.LAZY)

    //JoinColumn define la FK.
    //nullable = false. La columna NO admite NULL a nivel de la BD
    @JoinColumn(name = "country_id", nullable = false)
    @Setter(AccessLevel.NONE) // No se puede setear con setter ni con builder
    @ToString.Exclude // evita que Country se incluya en toString()
    private Country country;

    @Builder
    public Location(String name) {
        this.name = name;
    }

    void setCountry(Country country) {
        this.country = country;
    }

    public void addPerson(Person person) {
        this.people.add(person);
        person.setLocation(this);
    }
}
