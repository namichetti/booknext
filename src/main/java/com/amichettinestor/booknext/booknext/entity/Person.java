package com.amichettinestor.booknext.booknext.entity;


import jakarta.persistence.*;
import lombok.*;;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="people")
//Pasaje a superclase.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//Columna que identifica la subclase
@DiscriminatorColumn(name = "person_type")
public abstract class Person{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String lastName;

    //optional = false y  nullable = false me asegura que el lado N tenga mínimo 1 y no 0.

    //Este es el lado dueño y define la FK
    //optional = false. Indica que no es válida una referencia nula, es decir una location.
    //FetchType.LAZY  location no se carga automáticamente y se trae solo si se lo llama
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    //JoinColumn define la FK.
    //nullable = false. La columna NO admite NULL a nivel de la BD
    @JoinColumn(name = "location_id", nullable = false)
    @ToString.Exclude // evita que bookCategory se incluya en toString()
    private Location location;

}
