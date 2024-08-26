package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Order;
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

//    @Query("INSERT INTO orders (user_id, product_id, status, total_amount, quantity, price, delivery_charge, coupon_applied, delivery_date, payment_channel, transaction_id, created_at) " +
//            "VALUES (:userId, :productId, :status, :totalAmount, :quantity, :price, :deliveryCharge, :couponApplied, :deliveryDate, :paymentChannel, :transactionId, :createdAt) " +
//            "RETURNING id")
//    Mono<Long> insertOrder(long userId, long productId, String status, double totalAmount,
//                           int quantity, double price, double deliveryCharge, String couponApplied,
//                           LocalDate deliveryDate, String paymentChannel, String transactionId, Timestamp createdAt);
}
