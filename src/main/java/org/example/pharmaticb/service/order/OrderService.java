package org.example.pharmaticb.service.order;

import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface OrderService {
    Mono<OrderResponse> createOrder(OrderRequest request, AuthorizedUser authorizedUser);

    Flux<OrderResponse> getAllOrders();

    Mono<OrderResponse> getOrderById(long id, AuthorizedUser authorizedUser);

    Mono<OrderResponse> updateOrder(long id, OrderRequest request, AuthorizedUser authorizedUser);

    Mono<Void> deleteOrder(long id);

    Mono<OrderResponse> updateOrderStatus(OrderUpdateStatusRequest request, AuthorizedUser authorizedUser);

    Flux<OrderResponse> getOrdersWithinDate(LocalDate startDate, LocalDate effectiveEndDate);

    Flux<OrderResponse> getOrdersByStatus(String status);

    Mono<PagedResponse<OrderResponse>> getPageOrders(int page, int size, String sortBy, String sortDirection);
}
