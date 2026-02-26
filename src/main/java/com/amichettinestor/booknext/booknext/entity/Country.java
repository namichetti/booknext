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
@Table(name="countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //solo incluye este atributo en hascode y equeals. Con el id es suficiente
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    //MappedBy indica que la relación está definida por el atributo
    //country en la entidad Location
    @OneToMany(mappedBy = "country")
    @ToString.Exclude // evita que locations se incluya en toString()
    @Builder.Default
    private Set<Location> locations = new HashSet<>();

    @Builder
    public Country(String name) {
        this.name = name;
    }

    public void addLocation(Location location) {
        locations.add(location);
        location.setCountry(this);
    }
}
