package org.example.pharmaticb.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.dto.OrderItemDto.OrderItemDto;
import org.example.pharmaticb.dto.UserDto;
import org.example.pharmaticb.dto.enums.Status;
import org.example.pharmaticb.dto.records.Item;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.product.ProductServiceImpl;
import org.example.pharmaticb.service.user.UserServiceImpl;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final ProductServiceImpl productService;
    private final ProductRepository productRepository;
    private final UserServiceImpl userService;

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest request, AuthorizedUser authorizedUser) {
        long authorizedUserId = authorizedUser.getId();
        return Mono.zip(convertDtoToDb(request, Order.builder().build(), authorizedUserId), userService.getUserById(authorizedUserId))
                .flatMap(tuple2 -> {
                    var orderObj = tuple2.getT1();
                    var user = tuple2.getT2();
                    return orderRepository.save(orderObj)
                            .flatMap(order -> getProducts(order)
                                    .map(product -> convertDbToDto(order, product, user)));
                });
    }

    private Mono<Order> convertDtoToDb(OrderRequest request, Order order, long id) {
        return getTotalAmount(request.getItems())
                .map(totalAmount -> Order.builder()
                        .id(!ObjectUtils.isEmpty(order.getId()) ? order.getId() : null)
                        .userId(id)
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

    private Mono<Double> getTotalAmount(List<Item> items) {
        return Flux.fromIterable(items)
                .flatMap(this::processOrderItem)
                .reduce(0.0, Double::sum);

    }

    private Mono<Double> processOrderItem(Item item) {
        return productService.getProductById(item.productId())
                .map(productResponse -> calculateItemTotal(productResponse, item.quantity()));
    }

    private Double calculateItemTotal(ProductResponse productResponse, int quantity) {
        return (productResponse.getPrice() - productResponse.getDiscount()) * quantity;
    }

    @Override
    public Flux<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .flatMap(order -> getProducts(order)
                        .map(productResponse -> convertDbToDto(order, productResponse)));
    }

    private OrderResponse convertDbToDto(Order order, List<ProductResponse> product) {
        return OrderResponse.builder()
                .id(String.valueOf(order.getId()))
                .orderItems(getOrderItems(product, order))
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryCharge(order.getDeliveryCharge())
                .couponApplied(order.getCouponApplied())
                .deliveryDate(order.getDeliveryDate())
                .paymentChannel(order.getPaymentChannel())
                .transactionId(order.getTransactionId())
                .build();
    }

    private Mono<List<ProductResponse>> getProducts(Order order) {
        return Flux.fromIterable(getItems(order))
                .flatMap(item -> productService.getProductById(item.productId()))
                .collectList();
    }

    private OrderResponse convertDbToDto(Order order, List<ProductResponse> product, UserResponse user) {
        return OrderResponse.builder()
                .user(getUserDetails(user))
                .id(String.valueOf(order.getId()))
                .orderItems(getOrderItems(product, order))
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryCharge(order.getDeliveryCharge())
                .couponApplied(order.getCouponApplied())
                .deliveryDate(order.getDeliveryDate())
                .paymentChannel(order.getPaymentChannel())
                .transactionId(order.getTransactionId())
                .build();
    }

    private List<OrderItemDto> getOrderItems(List<ProductResponse> product, Order order) {
        return product.stream()
                .map(productResponse -> OrderItemDto.builder()
                        .productId(productResponse.getProductId())
                        .productName(productResponse.getProductName())
                        .unitPrice(String.valueOf(productResponse.getPrice()))
                        .quantity(getQuantity(getItems(order), productResponse.getProductId()))
                        .build())
                .collect(Collectors.toList());
    }

    private String getQuantity(List<Item> items, String productId) {
        return items.stream()
                .filter(item -> productId.equals(String.valueOf(item.productId())))
                .map(item -> String.valueOf(item.quantity()))
                .collect(Collectors.joining());
    }

    private UserDto getUserDetails(UserResponse user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .phone(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }

    private List<Item> getItems(Order order) {
        try {
            String jsonString = objectMapper.writeValueAsString(order.getItems());
            return List.of(objectMapper.readValue(jsonString, Item[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<OrderResponse> getOrderById(long id) {
        return orderRepository.findById(id)
                .flatMap(order -> Mono.zip(getProducts(order), userService.getUserById(order.getUserId()))
                        .map(tuple2 -> convertDbToDto(order, tuple2.getT1(), tuple2.getT2())));
    }

    @Override
    public Mono<OrderResponse> updateOrder(long id, OrderRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findById(id)
                .flatMap(order -> convertDtoToDb(request, order, authorizedUser.getId())
                        .flatMap(updatedOrder -> Mono.zip(userService.getUserById(authorizedUser.getId()),
                                orderRepository.save(updatedOrder),
                                getProducts(updatedOrder)))
                        .map(tuple3 -> convertDbToDto(tuple3.getT2(), tuple3.getT3(), tuple3.getT1())));
    }

    @Override
    public Mono<Void> deleteOrder(long id) {
        return orderRepository.deleteById(id);
    }

    @Override
    public Mono<OrderResponse> updateOrderStatus(OrderUpdateStatusRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findById(Long.valueOf(request.getOrderId()))
                .flatMap(order -> {
                    order.setStatus(request.getStatus());
                    if (Status.COMPLETED.name().equals(request.getStatus())) {
                        return Flux.fromIterable(getItems(order))
                                .flatMap(item -> productRepository.findById(item.productId())
                                        .flatMap(product -> {
                                            if (product.getStock() < item.quantity()) {
                                                return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Not available stock", ServiceError.INVALID_REQUEST));
                                            }
                                            product.setStock(product.getStock() - item.quantity());
                                            return productRepository.save(product);
                                        }))
                                .then(orderRepository.save(order));
                    }
                    return orderRepository.save(order);
                })
                .map(order -> OrderResponse.builder().status(order.getStatus()).build());
    }

    @Override
    public Flux<OrderResponse> getOrdersWithinDate(LocalDate startDate, LocalDate effectiveEndDate) {
        return orderRepository.findByCreatedAtBetween(startDate, effectiveEndDate)
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
    }
}
