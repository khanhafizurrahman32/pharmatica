package org.example.pharmaticb.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.Models.Response.UserResponse;
import org.example.pharmaticb.dto.AuthorizedUser;
import org.example.pharmaticb.dto.OrderItemDto.OrderItemDto;
import org.example.pharmaticb.dto.OrderWithDetails;
import org.example.pharmaticb.dto.UserDto;
import org.example.pharmaticb.dto.enums.OrderStatus;
import org.example.pharmaticb.dto.records.Item;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.product.ProductServiceImpl;
import org.example.pharmaticb.service.user.UserServiceImpl;
import org.example.pharmaticb.utilities.DateUtil;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.dto.enums.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.pharmaticb.utilities.DateUtil.currentTimeInDBTimeStamp;
import static org.example.pharmaticb.utilities.Utility.ROLE_PREFIX;

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
                        .status(OrderStatus.INITIATED.name())
                        .totalAmount(totalAmount)
                        .deliveryCharge(0.0) //todo
                        .couponApplied(request.getCouponApplied())
                        .deliveryDate(LocalDate.now())
                        .paymentChannel(request.getPaymentChannel())
                        .prescriptionUrl(request.getPrescriptionUrl())
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
                .prescriptionUrl(order.getPrescriptionUrl())
                .transactionId(order.getTransactionId())
                .orderDate(String.valueOf(order.getCreatedAt()))
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
                .prescriptionUrl(order.getPrescriptionUrl())
                .transactionId(order.getTransactionId())
                .orderDate(String.valueOf(order.getCreatedAt()))
                .build();
    }

    private List<OrderItemDto> getOrderItems(List<ProductResponse> product, Order order) {
        return product.stream()
                .map(productResponse -> OrderItemDto.builder()
                        .productId(productResponse.getProductId())
                        .productName(productResponse.getProductName())
                        .unitPrice(String.valueOf(productResponse.getPrice() - productResponse.getDiscount()))
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
    public Mono<OrderResponse> getOrderById(long id, AuthorizedUser authorizedUser) {
        return orderRepository.findById(id)
                .flatMap(order -> Mono.zip(getProducts(order), userService.getUserById(authorizedUser.getId()))
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
    public Mono<Void> deleteOrder(long id, AuthorizedUser authorizedUser) {
        if ((ROLE_PREFIX + Role.USER.name()).equals(authorizedUser.getRole())) {
            return orderRepository.findById(id)
                    .filter(order -> OrderStatus.INITIATED.name().equals(order.getStatus()) && authorizedUser.getId() == order.getUserId())
                    .flatMap(order -> orderRepository.deleteById(id).thenReturn(true))
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Order can not be deleted", "o1"))))
                    .then();
        }
        return orderRepository.deleteById(id);
    }

    @Override
    public Mono<OrderResponse> updateOrderStatus(OrderUpdateStatusRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findById(Long.valueOf(request.getOrderId()))
                .flatMap(order -> {
                    var currentStatus = OrderStatus.valueOf(order.getStatus().toUpperCase());
                    if (!currentStatus.canTransitionTo(OrderStatus.valueOf(request.getStatus().toUpperCase()))) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Order can not be updated", "o1"));
                    }

                    order.setStatus(request.getStatus());
                    if (OrderStatus.COMPLETED.name().equals(request.getStatus())) {
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
    public Flux<OrderResponse> getOrdersWithinDate(String startDate, String endDate) {
        long effectiveStartDate = DateUtil.convertIsoToTimestamp(startDate);
        long effectiveEndDate = StringUtils.hasText(endDate) ? DateUtil.convertIsoToTimestamp(endDate) : DateUtil.convertIsoToTimestamp(currentTimeInDBTimeStamp());
        return orderRepository.findByCreatedAtBetween(new Timestamp(effectiveStartDate), new Timestamp(effectiveEndDate))
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
    }

    @Override
    public Flux<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
    }

    @Override
    public Mono<PagedResponse<OrderResponse>> getPageOrders(int page, int size, String sortBy, String sortDirection) {
        if (page < 0 || size <= 0) {
            return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Invalid page or size parameters", ServiceError.INVALID_REQUEST));
        }

        Sort sort;
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            sort = Sort.by(direction, sortBy);
        } catch (IllegalArgumentException e) {
            return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, e.getMessage(), ServiceError.INVALID_REQUEST));
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Flux<OrderResponse> orders = orderRepository.findAllBy(pageRequest)
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
        return Mono.zip(orders.collectList(), orderRepository.count())
                .map(tuple2 -> PagedResponse.<OrderResponse>builder()
                        .content(tuple2.getT1())
                        .totalElements(tuple2.getT2())
                        .totalPages((int) Math.ceil((double) tuple2.getT2() / size))
                        .currentPage(page)
                        .size(size)
                        .build());
    }

    @Override
    public Flux<OrderResponse> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId)
                .flatMap(order -> getProducts(order)
                        .map(productResponse -> convertDbToDto(order, productResponse)))
                .sort(Comparator.comparing(OrderResponse::getOrderDate).reversed());
    }

    @Override
    public Flux<OrderResponse> getOrderDetails(String userId, String orderId, String productId) {
        return orderRepository.findAllOrdersWithDetails(StringUtils.hasText(userId) ?Long.parseLong(userId) : null,
                StringUtils.hasText(orderId) ?Long.parseLong(orderId) : null,
                StringUtils.hasText(productId) ?Long.parseLong(productId) : null)
                .map(orderWithDetails -> OrderResponse.builder()
                        .user(getUserDetails2(orderWithDetails))
                        .id(String.valueOf(orderWithDetails.getOrderId()))
                        //todo: start from here
                        .build());
    }

    private UserDto getUserDetails2(OrderWithDetails orderWithDetails) {
        return UserDto.builder()
                .id(String.valueOf(orderWithDetails.getUserId()))
                .userName(orderWithDetails.getUserName())
                .phone(orderWithDetails.getPhoneNumber())
                .address(orderWithDetails.getAddress())
                .build();
    }
}
