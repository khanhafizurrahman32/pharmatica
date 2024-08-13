package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.service.order.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public Mono<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/orders")
    public Flux<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Mono<OrderResponse> getOrderById(@Valid @PathVariable long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/orders/{id}")
    public Mono<OrderResponse> updateOrder(@Valid @PathVariable long id, @Valid @RequestBody OrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/orders/{id}")
    public Mono<Void> deleteOrder(@Valid @PathVariable long id) {
        return orderService.deleteOrder(id);
    }
}
