package com.amichettinestor.booknext.booknext.repository;

import com.amichettinestor.booknext.booknext.entity.Order;
import com.amichettinestor.booknext.booknext.entity.User;
import com.amichettinestor.booknext.booknext.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserUsername(String username);

    Optional<Order> findByUserAndStatus(User user, OrderStatus status);
}
