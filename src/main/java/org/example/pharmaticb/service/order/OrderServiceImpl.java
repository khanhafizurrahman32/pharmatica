package org.example.pharmaticb.service.order;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest request) {
        Order order = convertDtoToDb(request);
        return orderRepository.insertOrder(order.getUserId(), order.getProductId(), order.getStatus(), order.getTotalAmount(),
                order.getQuantity(), order.getPrice(), order.getDeliveryCharge(), order.getCouponApplied(),
                order.getDeliveryDate(), order.getPaymentChannel(), order.getTransactionId(), order.getCreatedAt())
                .map(id -> {
                    order.setId(id);
                    order.setNewOrder(true);
                    return order;
                })
                .map(this::convertDbToDto);
    }

    private Order convertDtoToDb(OrderRequest request) {
        return Order.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .status(request.getStatus())
                .totalAmount(request.getTotalAmount())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .deliveryCharge(request.getDeliveryCharge())
                .couponApplied(request.getCouponApplied())
                .deliveryDate(request.getDeliveryDate())
                .paymentChannel(request.getPaymentChannel())
                .transactionId("001")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    public Flux<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .map(this::convertDbToDto);
    }

    private OrderResponse convertDbToDto(Order order) {
        return OrderResponse.builder()
                .id(String.valueOf(order.getId()))
                .userId(String.valueOf(order.getUserId()))
                .productId(String.valueOf(order.getProductId()))
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .deliveryCharge(order.getDeliveryCharge())
                .couponApplied(order.getCouponApplied())
                .deliveryDate(order.getDeliveryDate())
                .paymentChannel(order.getPaymentChannel())
                .transactionId(order.getTransactionId())
                .build();
    }

    @Override
    public Mono<OrderResponse> getOrderById(long id) {
        return orderRepository.findById(id)
                .map(order -> mapper.map(order, OrderResponse.class));
    }

    @Override
    public Mono<OrderResponse> updateOrder(long id, OrderRequest request) {
        return orderRepository.findById(id)
                .flatMap(order -> {
                    BeanUtils.copyProperties(request, order);
                    order.setNewOrder(false);
                    return orderRepository.save(order);
                })
                .map(updatedOrder -> mapper.map(updatedOrder, OrderResponse.class));
    }

    @Override
    public Mono<Void> deleteOrder(long id) {
        return orderRepository.deleteById(id);
    }
}
