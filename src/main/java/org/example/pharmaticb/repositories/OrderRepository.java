package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Order;
import org.example.pharmaticb.dto.DailyOrderCount;
import org.example.pharmaticb.dto.OrderWithDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    @Query("SELECT * FROM orders where created_at >= :startDate AND created_at <= :endDate")
    Flux<Order> findByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    Flux<Order> findByStatus(String status);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findByUserId(Long userId);

    @Query("SELECT MAX(id) FROM Orders")
    Mono<Long> findLastProductId();


    @Query("WITH RECURSIVE date_range AS (\n" +
            "                SELECT :startDate AS date\n" +
            "                UNION ALL\n" +
            "                SELECT (date + INTERVAL '1 day')::DATE\n" +
            "                FROM date_range\n" +
            "                WHERE date < :endDate\n" +
            "            )\n" +
            "            SELECT\n" +
            "                dr.date,\n" +
            "                COALESCE(COUNT(o.created_at), 0) AS order_count\n" +
            "            FROM\n" +
            "                date_range dr\n" +
            "            LEFT JOIN\n" +
            "                pharmatic_prod.orders o ON DATE(o.created_at) = dr.date\n" +
            "                AND o.created_at >= :startDate\n" +
            "                AND o.created_at < :endDate\n" +
            "            GROUP BY\n" +
            "                dr.date\n" +
            "            ORDER BY\n" +
            "                dr.date DESC")
    Flux<DailyOrderCount> findDailyOrderCountsLastWeek(LocalDate startDate, LocalDate endDate);

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
        "    o.receipt_url,\n" +
        "    o.created_at,\n" +
        "    o.delivery_options_id,\n" +
        "    o.items,\n" +
        "    u.id AS user_id,\n" +
        "    u.user_name,\n" +
        "    u.phone_number,\n" +
        "    u.address,\n" +
        "    u.email,\n" +
        "    d.id AS deliveryItemId,\n" +
        "    d.title,\n" +
        "    d.rate,\n" +
        "    jsonb_agg(\n" +
        "        jsonb_build_object(\n" +
        "            'product_id', (items.item->>'productId')::integer,\n" +
        "            'quantity', (items.item->>'quantity')::integer,\n" +
        "            'product_name', p.product_name,\n" +
        "            'price', p.price,\n" +
        "            'discount', p.discount\n" +
        "        )\n" +
        "    ) AS product_details\n" +
        "FROM\n" +
        "    pharmatic_prod.orders o\n" +
        "CROSS JOIN LATERAL\n" +
        "    jsonb_array_elements(o.items::jsonb) AS items(item)\n" +
        "JOIN\n" +
        "    pharmatic_prod.users u ON o.user_id = u.id\n" +
        "JOIN\n" +
        "    pharmatic_prod.delivery_type d ON o.delivery_options_id = d.id\n" +
        "LEFT JOIN\n" +
        "    pharmatic_prod.product p ON (items.item->>'productId')::integer = p.id\n" +
        "WHERE \n" +
        "    (:userId IS NULL OR u.id = :userId)\n" +
        "    AND (:productId IS NULL OR (items.item->>'productId')::integer = :productId)\n" +
        "    AND (:orderId IS NULL OR o.id = :orderId)\n" +
        "    AND (:startDate IS NULL OR o.created_at >= :startDate)\n" +
        "    AND (:endDate IS NULL OR o.created_at <= :endDate)\n" +
        "GROUP BY\n" +
        "    o.id, u.id, d.id\n" +
        "ORDER BY\n" +
        "    o.created_at DESC")
Flux<OrderWithDetails> findAllOrdersWithDetails(Long userId, Long orderId, Long productId, Timestamp startDate, Timestamp endDate);

//    @Query("INSERT INTO orders (user_id, product_id, status, total_amount, quantity, price, delivery_charge, coupon_applied, delivery_date, payment_channel, transaction_id, created_at) " +
//            "VALUES (:userId, :productId, :status, :totalAmount, :quantity, :price, :deliveryCharge, :couponApplied, :deliveryDate, :paymentChannel, :transactionId, :createdAt) " +
//            "RETURNING id")
//    Mono<Long> insertOrder(long userId, long productId, String status, double totalAmount,
//                           int quantity, double price, double deliveryCharge, String couponApplied,
//                           LocalDate deliveryDate, String paymentChannel, String transactionId, Timestamp createdAt);
}
