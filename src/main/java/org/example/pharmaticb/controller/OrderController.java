package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.service.order.OrderService;
import org.example.pharmaticb.utilities.Utility;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@BaseController
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public Mono<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request, Principal principal) {
        return orderService.createOrder(request, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @PostMapping("/orders/update-status")
    public Mono<OrderResponse> updateOrderStatus(@Valid @RequestBody OrderUpdateStatusRequest request, Principal principal) {
        return orderService.updateOrderStatus(request, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @GetMapping("/orders")
    public Flux<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Mono<OrderResponse> getOrderById(@Valid @PathVariable long id, Principal principal) {
        return orderService.getOrderById(id, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @PutMapping("/orders/{id}")
    public Mono<OrderResponse> updateOrder(@Valid @PathVariable long id, @Valid @RequestBody OrderRequest request, Principal principal) {
        return orderService.updateOrder(id, request, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @GetMapping("/orders/within-date")
    public Flux<OrderResponse> getOrdersWithinDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
                                                   @RequestParam(required = false) @DateTimeFormat() String endDate) {
        return orderService.getOrdersWithinDate(startDate, endDate);
    }

    @GetMapping("/orders/by-status")
    public Flux<OrderResponse> getOrdersByStatus(@RequestParam String status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/orders/paginated")
    public Mono<PagedResponse<OrderResponse>> getOrdersPaginated(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sortBy,
                                                                @RequestParam(defaultValue = "ASC") String sortDirection) {
        return orderService.getPageOrders(page, size, sortBy, sortDirection);
    }

    @DeleteMapping("/orders/{id}")
    public Mono<Void> deleteOrder(@Valid @PathVariable long id, Principal principal) {
        return orderService.deleteOrder(id, Utility.extractAuthorizedUserFromPrincipal(principal));
    }

    @GetMapping("/orders/user/{userId}")
    public Flux<OrderResponse> getOrdersByUserId(@PathVariable long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
