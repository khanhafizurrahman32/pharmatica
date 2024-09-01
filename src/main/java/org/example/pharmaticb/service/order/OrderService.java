package org.example.pharmaticb.service.order;

import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.dto.OrderWithDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<OrderResponse> createOrder(OrderRequest request, AuthorizedUser authorizedUser);

    Flux<OrderResponse> getAllOrders();

    Mono<OrderResponse> getOrderById(long id, AuthorizedUser authorizedUser);

    Mono<OrderResponse> updateOrder(long id, OrderRequest request, AuthorizedUser authorizedUser);

    Mono<Void> deleteOrder(long id, AuthorizedUser authorizedUser);

    Mono<OrderResponse> updateOrderStatus(OrderUpdateStatusRequest request, AuthorizedUser authorizedUser);

    Flux<OrderResponse> getOrdersWithinDate(String startDate, String endDate);

    Flux<OrderResponse> getOrdersByStatus(String status);

    Mono<PagedResponse<OrderResponse>> getPageOrders(int page, int size, String sortBy, String sortDirection);

    Flux<OrderResponse> getOrdersByUserId(long userId);

    Flux<OrderResponse> getOrderDetails(String userId, String orderId, String productId);
}
