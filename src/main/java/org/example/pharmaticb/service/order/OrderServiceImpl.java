package org.example.pharmaticb.service.order;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest request) {
        return orderRepository.save(mapper.map(request, Order.class))
                .map(order -> mapper.map(order, OrderResponse.class));
    }

    @Override
    public Flux<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .map(order -> mapper.map(order, OrderResponse.class));
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
                    mapper.map(request, order);
                    return orderRepository.save(order)
                            .map(order1 -> mapper.map(order1, OrderResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteOrder(long id) {
        return orderRepository.deleteById(id);
    }
}
