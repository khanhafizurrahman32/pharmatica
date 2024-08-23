package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order implements Serializable {
    @Id
    private Long id;
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
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String transactionId;
    private Timestamp createdAt;

    @Transient
    @Builder.Default
    private boolean newOrder = true;

    public boolean isNewOrder() {
        return this.newOrder || id == null;
    }

}
