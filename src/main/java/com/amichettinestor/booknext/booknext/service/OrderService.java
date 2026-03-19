package com.amichettinestor.booknext.booknext.service;

import com.amichettinestor.booknext.booknext.dto.*;

import java.util.List;

public interface OrderService {

    void save(List<OrderRequestDto> orderRequestDto);

    void deleteById(Long id);

    void patchOrder(Long id, OrderUpdateDto orderUpdateDto);

    OrderResponseDto findById(Long id);

    void changeStatus(Long id, ChangeStatusOrderRequestDto statusUserRequestDto);

    List<OrderResponseDto> findAll();
}
