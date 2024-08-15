package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order implements Serializable {
    @Id
    @Generated
    private long id;
//    FK
    private long userId;
//    FK
    private long productId;
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
