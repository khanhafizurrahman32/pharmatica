package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse implements Serializable {
    private String id;
    private String userId;
    private String productId;
    private String status;
    private double totalAmount;
    private int quantity;
    private double price;
    private double deliveryCharge;
    private String couponApplied;
    private Date deliveryDate;
    private String paymentChannel;
    private String transactionId;
}
