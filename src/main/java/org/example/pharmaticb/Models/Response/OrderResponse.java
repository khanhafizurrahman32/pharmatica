package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.dto.records.Item;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse implements Serializable {
    private String id;
    private String userId;
    private Item[] items;
    private String productId;
    private String status;
    private double totalAmount;
    private int quantity;
    private double deliveryCharge;
    private String couponApplied;
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String transactionId;
}
