package org.example.pharmaticb.service.order;

import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<OrderResponse> createOrder(OrderRequest request);

    Flux<OrderResponse> getAllOrders();

    Mono<OrderResponse> getOrderById(long id);

    Mono<OrderResponse> updateOrder(long id, OrderRequest request);

    Mono<Void> deleteOrder(long id);
}
