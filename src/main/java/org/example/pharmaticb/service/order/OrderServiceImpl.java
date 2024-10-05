package org.example.pharmaticb.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.Models.Request.OrderRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateDeliveryChargeRequest;
import org.example.pharmaticb.Models.Request.OrderUpdateStatusRequest;
import org.example.pharmaticb.Models.Response.*;
import org.example.pharmaticb.dto.*;
import org.example.pharmaticb.dto.OrderItemDto.OrderItemDto;
import org.example.pharmaticb.dto.enums.OrderStatus;
import org.example.pharmaticb.dto.enums.Role;
import org.example.pharmaticb.dto.records.Item;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.OrderRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.barcode.BarcodeService;
import org.example.pharmaticb.service.delivery.type.DeliveryTypeService;
import org.example.pharmaticb.service.email.EmailService;
import org.example.pharmaticb.service.file.FileUploadService;
import org.example.pharmaticb.service.product.ProductServiceImpl;
import org.example.pharmaticb.service.receipt.ReceiptGenerationService;
import org.example.pharmaticb.service.user.UserService;
import org.example.pharmaticb.utilities.DateUtil;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.example.pharmaticb.utilities.Utility;
import org.example.pharmaticb.utilities.log.Loggable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final UserService userService;
    private final DeliveryTypeService deliveryTypeService;
    private final BarcodeService barcodeService;
    private final FileUploadService fileUploadService;
    private final ReceiptGenerationService receiptGenerationService;
    private final EmailService emailService;

    @Override
    @Loggable
    public Mono<OrderResponse> createOrder(OrderRequest request, AuthorizedUser authorizedUser) {
        long authorizedUserId = authorizedUser.getId();
        return Mono.zip(convertDtoToDb(request, Order.builder().build(), authorizedUserId, true), userService.getUserById(authorizedUserId))
                .flatMap(tuple2 -> {
                    var orderObj = tuple2.getT1();
                    var user = tuple2.getT2();
                    if (Boolean.parseBoolean(user.getDeactivated())) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "User is deactivated", ServiceError.DEACTIVATED_USER));
                    }
                    return orderRepository.save(orderObj)
                            .flatMap(order -> getProducts(order)
                                    .map(product -> {
                                        Schedulers.boundedElastic().schedule(() -> emailService.sendEmail("pharmatic24@gmail.com", "New order", "A new order has been placed").subscribe());
                                        return convertDbToDto(order, product, user);
                                    }));
                });
    }

    private Mono<Order> convertDtoToDb(OrderRequest request, Order order, long userId, boolean isNew) {
        return Mono.zip(getTotalAmount(request.getItems()), getDeliveryCharge(request, isNew, order), orderRepository.findLastOrderId())
                .map(tuple3 -> Order.builder()
                        .id(!ObjectUtils.isEmpty(order.getId()) ? order.getId() : null)
                        .userId(userId)
                        .items(objectMapper.valueToTree(request.getItems()))
                        .status(OrderStatus.INITIATED.name())
                        .totalAmount(tuple3.getT1())
                        .deliveryOptionsId(Long.parseLong(request.getDeliveryOptionId()))
                        .deliveryCharge(Double.parseDouble(tuple3.getT2()))
                        .couponApplied(request.getCouponApplied())
                        .deliveryDate(LocalDate.now())
                        .paymentChannel(request.getPaymentChannel())
                        .prescriptionUrl(request.getPrescriptionUrl())
                        .transactionId(getTransactionId(tuple3.getT3()))
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .build())
                .doOnError(error -> log.error("convertDtoToDb error", error));

    }

    private String getTransactionId(Long lastId) {
        return String.format("%S%d", DateUtil.getTransactionIdDate(), lastId + 1);
    }

    private Mono<Double> getTotalAmount(List<Item> items) {
        log.info("inside total amount");
        return Flux.fromIterable(items)
                .flatMap(this::processOrderItem)
                .doOnError(error -> log.info("error in get Total Amount", error))
                .reduce(0.0, Double::sum);
    }

    private Mono<String> getDeliveryCharge(OrderRequest request, boolean isNew, Order order) {
        log.info("inside getDeliveryCharge");
        return isNew ? deliveryTypeService.getDeliveryChargeTypeById(Long.valueOf(request.getDeliveryOptionId()))
                .map(DeliveryTypeResponse::getRate) : StringUtils.hasText(request.getDeliveryCharge()) ?
                Mono.just(request.getDeliveryCharge()) : Mono.just(String.valueOf(order.getDeliveryCharge()))
                .doOnError(error -> log.error("error in delivery charge", error));
    }

    private Mono<Double> processOrderItem(Item item) {
        return productService.getProductById(item.productId())
                .map(productResponse -> calculateItemTotal(productResponse, item.quantity()));
    }

    private Double calculateItemTotal(ProductResponse productResponse, int quantity) {
        return (productResponse.getPrice() - productResponse.getDiscount()) * quantity;
    }

    @Override
    @Loggable
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
                .receiptUrl(order.getReceiptUrl())
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
                .receiptUrl(order.getReceiptUrl())
                .transactionId(order.getTransactionId())
                .orderDate(String.valueOf(order.getCreatedAt()))
                .build();
    }

    private List<OrderItemDto> getOrderItems(List<ProductResponse> product, Order order) {
        return product.stream()
                .map(productResponse -> {
                    var unitPrice = BigDecimal.valueOf(productResponse.getPrice() - productResponse.getDiscount()).setScale(2, RoundingMode.HALF_UP);
                    var quantity = getQuantity(getItems(order), productResponse.getProductId());
                    return OrderItemDto.builder()
                            .productId(productResponse.getProductId())
                            .productName(productResponse.getProductName())
                            .unitPrice(unitPrice)
                            .quantity(quantity)
                            .totalPrice(unitPrice.multiply(quantity))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private BigDecimal getQuantity(List<Item> items, String productId) {
        return items.stream()
                .filter(item -> productId.equals(String.valueOf(item.productId())))
                .findFirst()
                .map(item -> BigDecimal.valueOf(item.quantity()))
                .orElse(BigDecimal.valueOf(0.0));
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
        if (order == null || order.getItems() == null) {
            return Collections.emptyList();
        }

        try {
            String jsonString = objectMapper.writeValueAsString(order.getItems());
            return List.of(objectMapper.readValue(jsonString, Item[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Loggable
    public Mono<OrderResponse> getOrderById(long id, AuthorizedUser authorizedUser) {
        return orderRepository.findById(id)
                .flatMap(order -> getProducts(order)
                        .flatMap(productResponses -> userService.getUserById(order.getUserId())
                                .map(userResponse -> convertDbToDto(order, productResponses, userResponse))));
    }

    @Override
    @Loggable
    public Mono<OrderResponse> updateOrder(long id, OrderRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findById(id)
                .flatMap(order -> convertDtoToDb(request, order, authorizedUser.getId(), false)
                        .flatMap(updatedOrder -> Mono.zip(userService.getUserById(authorizedUser.getId()),
                                orderRepository.save(updatedOrder),
                                getProducts(updatedOrder)))
                        .map(tuple3 -> convertDbToDto(tuple3.getT2(), tuple3.getT3(), tuple3.getT1())));
    }

    @Override
    @Loggable
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
    @Loggable
    public Mono<OrderResponse> updateOrderStatus(OrderUpdateStatusRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findAllOrdersWithDetails(null, Long.valueOf(request.getOrderId()), null, null, null)
                .map(orderWithDetails -> updateOrderStatus(orderWithDetails, request.getStatus()))
                .flatMap(orderMono -> orderMono)
                .next()
                .map(order -> OrderResponse.builder().status(order.getStatus()).build())
                .onErrorResume(e -> {
                    if (e instanceof NoSuchElementException) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "No orders found", "NO_ORDER_FOUND"));
                    } else if (e instanceof IndexOutOfBoundsException) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "More orders found of single id", "MULTIPLE_ORDER_FOUND"));
                    }
                    return Mono.error(e);
                });
    }

    @Override
    @Loggable
    public Mono<OrderResponse> updateOrderDeliveryCharge(OrderUpdateDeliveryChargeRequest request, AuthorizedUser authorizedUser) {
        return orderRepository.findById(Long.valueOf(request.getOrderId()))
                .flatMap(order -> {
                    order.setDeliveryCharge(Double.parseDouble(request.getDeliveryCharge()));
                    return orderRepository.save(order);
                })
                .map(order -> OrderResponse.builder().deliveryCharge(order.getDeliveryCharge()).build());
    }

    private Mono<String> getBarcodeImageInfo(String transactionId) {
        var barcodeImage = barcodeService.generateBarcode(transactionId);
        return ObjectUtils.isEmpty(barcodeImage) ? Mono.just(transactionId) : fileUploadService.uploadFile(transactionId, barcodeImage, "images/png");
    }

    private Mono<Order> updateOrderStatus(OrderWithDetails orderWithDetails, String newStatus) {
        OrderStatus currentStatus = OrderStatus.valueOf(orderWithDetails.getStatus().toUpperCase());
        OrderStatus requestedStatus = OrderStatus.valueOf(newStatus.toUpperCase());

        if (!currentStatus.canTransitionTo(requestedStatus)) {
            return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Order can not be updated", "o1"));
        }

        Order order = buildOrder(orderWithDetails, newStatus);

        return switch (requestedStatus) {
            case ON_THE_WAY -> handleOnTheWayStatus(orderWithDetails, order);
            case COMPLETED -> handleCompletedStatus(order);
            default -> orderRepository.save(order);
        };
    }

    private Order buildOrder(OrderWithDetails orderWithDetails, String newStatus) {
        JsonNode itemsNode;
        try {
            itemsNode = objectMapper.readTree(orderWithDetails.getItems());
        } catch (JsonProcessingException e) {
            log.error("build order json error {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return Order.builder()
                .id(orderWithDetails.getOrderId())
                .userId(orderWithDetails.getUserId())
                .status(newStatus)
                .totalAmount(Double.parseDouble(orderWithDetails.getTotalAmount()))
                .deliveryCharge(Double.parseDouble(orderWithDetails.getDeliveryCharge()))
                .couponApplied(orderWithDetails.getCouponApplied())
                .deliveryDate(orderWithDetails.getDeliveryDate())
                .paymentChannel(orderWithDetails.getPaymentChannel())
                .transactionId(orderWithDetails.getTransactionId())
                .createdAt(orderWithDetails.getCreatedAt())
                .items(itemsNode)
                .prescriptionUrl(orderWithDetails.getPrescriptionUrl())
                .deliveryOptionsId(orderWithDetails.getDeliveryOptionsId())
                .receiptUrl(orderWithDetails.getReceiptUrl())
                .build();
    }

    private Mono<Order> handleCompletedStatus(Order order) {
        return Flux.fromIterable(getItems(order))
                .flatMap(this::updateProductStock)
                .then(orderRepository.save(order));
    }

    private Mono<Product> updateProductStock(Item item) {
        return productRepository.findById(item.productId())
                .flatMap(product -> {
                    if (product.getStock() < item.quantity()) {
                        return Mono.error(new InternalException(HttpStatus.BAD_REQUEST, "Not available stock", ServiceError.INVALID_REQUEST));
                    }
                    product.setStock(product.getStock() - item.quantity());
                    return productRepository.save(product);
                });
    }

    private Mono<Order> handleOnTheWayStatus(OrderWithDetails orderWithDetails, Order order) {
        return getBarcodeImageInfo(orderWithDetails.getTransactionId())
                .flatMap(barcodeUrl -> {
                    List<OrderItemDto> orderItems = getOrderItems(orderWithDetails);
                    ReceiptGenerationDto receiptGenerationDto = getReceiptGenerationDto(orderWithDetails, barcodeUrl, orderItems);
                    return receiptGenerationService.generateReceiptPdf(receiptGenerationDto)
                            .map(pdfUrl -> {
                                order.setReceiptUrl(pdfUrl);
                                return order;
                            })
                            .flatMap(orderRepository::save);
                });

    }

    private ReceiptGenerationDto getReceiptGenerationDto(OrderWithDetails orderWithDetails, String barcodeUrl, List<OrderItemDto> orderItems) {

        return ReceiptGenerationDto.builder()
                .companyLogo(Utility.COMPANY_LOGO)
                .barcodeLogo(barcodeUrl)
                .billId(orderWithDetails.getTransactionId())
                .customerName(orderWithDetails.getUserName())
                .transactionDate(DateUtil.getReceiptDate())
                .address(orderWithDetails.getAddress())
                .phoneNumber(orderWithDetails.getPhoneNumber())
                .email(orderWithDetails.getEmail())
                .orderItems(orderItems)
                .trxId(orderWithDetails.getTransactionId())
                .deliveryCharge(orderWithDetails.getDeliveryCharge())
                .totalPrice(String.valueOf(getTotalPrice(orderItems).add(new BigDecimal(orderWithDetails.getDeliveryCharge()))))
                .build();
    }

    private BigDecimal getTotalPrice(List<OrderItemDto> orderItems) {
        return orderItems.stream()
                .map(OrderItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Loggable
    public Flux<OrderResponse> getOrdersWithinDate(String startDate, String endDate) {
        long effectiveStartDate = DateUtil.convertIsoToTimestamp(startDate, false);
        long effectiveEndDate = StringUtils.hasText(endDate) ? DateUtil.convertIsoToTimestamp(endDate, true) : DateUtil.convertIsoToTimestamp(currentTimeInDBTimeStamp(), true);
        return orderRepository.findByCreatedAtBetween(new Timestamp(effectiveStartDate), new Timestamp(effectiveEndDate))
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
    }

    @Override
    @Loggable
    public Flux<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .flatMap(order -> getProducts(order)
                        .map(productResponses -> convertDbToDto(order, productResponses)));
    }

    @Override
    @Loggable
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
    @Loggable
    public Flux<OrderResponse> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId)
                .flatMap(order -> getProducts(order)
                        .map(productResponse -> convertDbToDto(order, productResponse)))
                .sort(Comparator.comparing(OrderResponse::getOrderDate).reversed());
    }

    @Override
    @Loggable
    public Flux<OrderResponse> getOrderDetails(String userId, String orderId, String productId, String startDate, String endDate) {
        long effectiveStartDate = DateUtil.convertIsoToTimestamp(startDate, false);
        long effectiveEndDate = StringUtils.hasText(endDate) ? DateUtil.convertIsoToTimestamp(endDate, true) : DateUtil.convertIsoToTimestamp(currentTimeInDBTimeStamp(), true);
        log.info("start date : {}, end date : {}", effectiveStartDate, effectiveEndDate);
        return orderRepository.findAllOrdersWithDetails(StringUtils.hasText(userId) ? Long.parseLong(userId) : null,
                        StringUtils.hasText(orderId) ? Long.parseLong(orderId) : null,
                        StringUtils.hasText(productId) ? Long.parseLong(productId) : null,
                        new Timestamp(effectiveStartDate), new Timestamp(effectiveEndDate)
                )
                .map(orderWithDetails -> OrderResponse.builder()
                        .user(getUserDetails2(orderWithDetails))
                        .id(String.valueOf(orderWithDetails.getOrderId()))
                        .status(orderWithDetails.getStatus())
                        .totalAmount(Double.parseDouble(orderWithDetails.getTotalAmount()))
                        .deliveryCharge(Double.parseDouble(orderWithDetails.getDeliveryCharge()))
                        .couponApplied(orderWithDetails.getCouponApplied())
                        .deliveryDate(orderWithDetails.getDeliveryDate())
                        .paymentChannel(orderWithDetails.getPaymentChannel())
                        .prescriptionUrl(orderWithDetails.getPrescriptionUrl())
                        .receiptUrl(orderWithDetails.getReceiptUrl())
                        .transactionId(orderWithDetails.getTransactionId())
                        .orderDate(String.valueOf(orderWithDetails.getCreatedAt()))
                        .orderItems(getOrderItems(orderWithDetails))
                        .deliveryItemType(getDeliveryItemType(orderWithDetails))
                        .build());
    }

    private DeliveryItemTypeDto getDeliveryItemType(OrderWithDetails orderWithDetails) {
        return DeliveryItemTypeDto.builder()
                .id(orderWithDetails.getDeliveryItemId())
                .title(orderWithDetails.getTitle())
                .rate(String.valueOf(orderWithDetails.getRate()))
                .build();
    }

    private List<OrderItemDto> getOrderItems(OrderWithDetails orderWithDetails) {
        try {
            List<ProductInfos> orderItems = objectMapper.readValue(orderWithDetails.getProductDetails(), new TypeReference<>() {
            });
            return orderItems
                    .stream()
                    .map(productInfos -> {
                        var unitPrice = BigDecimal.valueOf(productInfos.getPrice() - productInfos.getDiscount()).setScale(2, RoundingMode.HALF_UP);
                        var quantity = BigDecimal.valueOf(productInfos.getQuantity());

                        return OrderItemDto.builder()
                                .productId(productInfos.getProductId())
                                .productName(productInfos.getProductName())
                                .unitPrice(unitPrice)
                                .quantity(quantity)
                                .totalPrice(unitPrice.multiply(quantity))
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
