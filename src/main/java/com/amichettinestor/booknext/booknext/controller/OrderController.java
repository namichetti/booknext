package com.amichettinestor.booknext.booknext.controller;

import com.amichettinestor.booknext.booknext.dto.ChangeStatusOrderRequestDto;
import com.amichettinestor.booknext.booknext.dto.OrderRequestDto;
import com.amichettinestor.booknext.booknext.dto.OrderResponseDto;
import com.amichettinestor.booknext.booknext.dto.OrderUpdateDto;
import com.amichettinestor.booknext.booknext.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrderOrders() {
        var orders = orderService.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        var order = this.orderService.findById(id);
        return ResponseEntity.ok(order);
    }


    @PostMapping
    public ResponseEntity<String> saveOrder(@RequestBody List<@Valid OrderRequestDto> orderRequestDto){
        this.orderService.save(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("La orden se ha guardado correctamente.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        this.orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<String> patchOrder(@PathVariable Long id,
                                             @Valid @RequestBody OrderUpdateDto orderUpdateDto) {
        this.orderService.patchOrder(id,orderUpdateDto);
        return ResponseEntity.ok("La orden se actualizó correctamente.");
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<String> changeStatusByManager(@PathVariable Long id,
                                                        @Valid @RequestBody ChangeStatusOrderRequestDto statusOrderRequestDto) {
        this.orderService.changeStatus(id, statusOrderRequestDto);
        return ResponseEntity.ok("Se ha actualizado el estado de la orden");
    }

}
