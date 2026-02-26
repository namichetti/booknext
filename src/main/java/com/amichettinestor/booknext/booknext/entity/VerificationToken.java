package com.amichettinestor.booknext.booknext.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Ignorá todos los campos excepto los que tienen @EqualsAndHashCode.Include
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="Verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String token;

    //Dueño de la relación ya que tiene la FK
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private LocalDateTime expiresAt;
}
