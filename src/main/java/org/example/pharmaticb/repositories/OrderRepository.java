package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.dto.OrderWithDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.time.LocalDate;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    @Query("SELECT * FROM orders where created_at >= :startDate AND created_at <= :endDate")
    Flux<Order> findByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    Flux<Order> findByStatus(String status);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findByUserId(Long userId);

    @Query("SELECT\n" +
            "    o.id AS order_id,\n" +
            "    o.status,\n" +
            "    o.total_amount,\n" +
            "    o.delivery_charge,\n" +
            "    o.coupon_applied,\n" +
            "    o.delivery_date,\n" +
            "    o.payment_channel,\n" +
            "    o.prescription_url,\n" +
            "    o.transaction_id,\n" +
            "    o.created_at,\n" +
            "    u.id AS user_id,\n" +
            "    u.user_name,\n" +
            "    u.phone_number,\n" +
            "    u.address,\n" +
            "    (items.item->>'productId')::integer AS product_id,\n" +
            "    (items.item->>'quantity')::integer AS quantity,\n" +
            "    p.product_name AS product_name,\n" +
            "    p.price,\n" +
            "    p.discount\n" +
            "    \n" +
            "FROM\n" +
            "    pharmatica.orders o\n" +
            "CROSS JOIN LATERAL\n" +
            "    jsonb_array_elements(o.items::jsonb) AS items(item)\n" +
            "JOIN\n" +
            "    pharmatica.users u ON o.user_id = u.id\n" +
            "LEFT JOIN\n" +
            "    pharmatica.product p ON (items.item->>'productId')::integer = p.id\n" +
            "WHERE (:userId IS NULL OR u.id = :userId) " +
            "AND (:productId IS NULL OR (items.item->>'productId')::integer = :productId) " +
            "AND (:orderId IS NULL OR o.id = :orderId) " +
            "ORDER BY\n" +
            "    o.created_at DESC")
    Flux<OrderWithDetails> findAllOrdersWithDetails(Long userId, Long orderId, Long productId);

//    @Query("INSERT INTO orders (user_id, product_id, status, total_amount, quantity, price, delivery_charge, coupon_applied, delivery_date, payment_channel, transaction_id, created_at) " +
//            "VALUES (:userId, :productId, :status, :totalAmount, :quantity, :price, :deliveryCharge, :couponApplied, :deliveryDate, :paymentChannel, :transactionId, :createdAt) " +
//            "RETURNING id")
//    Mono<Long> insertOrder(long userId, long productId, String status, double totalAmount,
//                           int quantity, double price, double deliveryCharge, String couponApplied,
//                           LocalDate deliveryDate, String paymentChannel, String transactionId, Timestamp createdAt);
}
