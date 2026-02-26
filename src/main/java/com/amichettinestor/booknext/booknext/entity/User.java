package com.amichettinestor.booknext.booknext.entity;

import com.amichettinestor.booknext.booknext.enums.Role;
import com.amichettinestor.booknext.booknext.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//La columna person_type tendrá "AUTHOR"
@DiscriminatorValue("USER")
public class User extends Person{

    private String username;
    private String password;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    void addOrder(Order order){
        this.orders.add(order);
        order.setUser(this);
    }


    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = UserStatus.INACTIVE; // Establece como INACTIVE al crear el User
        }
    }

}
