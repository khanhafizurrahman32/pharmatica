package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    @Query("INSERT INTO orders (user_id, product_id, status, total_amount, quantity, price, delivery_charge, coupon_applied, delivery_date, payment_channel, transaction_id, created_at) " +
            "VALUES (:userId, :productId, :status, :totalAmount, :quantity, :price, :deliveryCharge, :couponApplied, :deliveryDate, :paymentChannel, :transactionId, :createdAt) " +
            "RETURNING id")
    Mono<Long> insertOrder(long userId, long productId, String status, double totalAmount,
                           int quantity, double price, double deliveryCharge, String couponApplied,
                           LocalDate deliveryDate, String paymentChannel, String transactionId, Timestamp createdAt);
}
