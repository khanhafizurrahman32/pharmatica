package org.example.pharmaticb.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.dto.records.Item;
import org.example.pharmaticb.dto.enums.Status;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.service.product.ProductServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ProductServiceImpl productServiceImpl;

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest request) {
        return convertDtoToDb(request, Order.builder().build())
                .flatMap(order -> orderRepository.save(order)
                        .map(this::convertDbToDto));
    }

    private Mono<Order> convertDtoToDb(OrderRequest request, Order order) {
        return getTotalAmount(request.getItems())
                .map(totalAmount -> Order.builder()
                        .id(!ObjectUtils.isEmpty(order.getId()) ? order.getId() : null)
                        .userId(request.getUserId())
                        .items(objectMapper.valueToTree(request.getItems()))
                        .status(Status.INITIATED.name())
                        .totalAmount(totalAmount)
                        .deliveryCharge(0.0) //todo
                        .couponApplied(request.getCouponApplied())
                        .deliveryDate(LocalDate.now())
                        .paymentChannel(request.getPaymentChannel())
                        .transactionId(UUID.randomUUID().toString().substring(0, 10))
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .build());

    }

    private Mono<Double> getTotalAmount(Item[] items) {
        return Flux.fromIterable(Arrays.asList(items))
                .flatMap(this::processOrderItem)
                .reduce(0.0, Double::sum);

    }

    private Mono<Double> processOrderItem(Item item) {
        return productServiceImpl.getProductById(item.productId())
                .map(productResponse -> calculateItemTotal(productResponse, item.quantity()));
    }

    private Double calculateItemTotal(ProductResponse productResponse, int quantity) {
        return (productResponse.getPrice() - productResponse.getDiscount()) * quantity;
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
                .items(getItems(order))
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryCharge(order.getDeliveryCharge())
                .couponApplied(order.getCouponApplied())
                .deliveryDate(order.getDeliveryDate())
                .paymentChannel(order.getPaymentChannel())
                .transactionId(order.getTransactionId())
                .build();
    }

    private Item[] getItems(Order order) {
        try {
            String jsonString = objectMapper.writeValueAsString(order.getItems());
            return objectMapper.readValue(jsonString, Item[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<OrderResponse> getOrderById(long id) {
        return orderRepository.findById(id)
                .map(order -> mapper.map(order, OrderResponse.class));
    }

    @Override
    public Mono<OrderResponse> updateOrder(long id, OrderRequest request) {
        return orderRepository.findById(id)
                .flatMap(order -> convertDtoToDb(request, order)
                        .flatMap(orderRepository::save)
                        .map(this::convertDbToDto));
    }

    @Override
    public Mono<Void> deleteOrder(long id) {
        return orderRepository.deleteById(id);
    }
}
