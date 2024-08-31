package org.example.pharmaticb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderWithDetails implements Serializable {
    private Long orderId;
    private String status;
    private String totalAmount;
    private String deliveryCharge;
    private String couponApplied;
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String transactionId;
    private Timestamp createdAt;

    private long userId;
    private String userName;
    private String phoneNumber;
    private String address;

    private String productId;
    private String quantity;
    private String productName;
    private String price;
    private String discount;
}
