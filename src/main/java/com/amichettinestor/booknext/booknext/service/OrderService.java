package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {

    void save(OrderRequestDto orderRequestDto);

    void deleteById(Long id);

    void patchOrder(Long id, OrderUpdateDto orderUpdateDto);

    void putOrder(Long id, OrderUpdateDto orderUpdateDto);

    OrderResponseDto findById(Long id);

    void changeStatusByManager(Long id, ManagerChangeStatusOrderRequestDto statusUserRequestDto);

    List<OrderResponseDto> findAll();
}
