package org.example.pharmaticb.Models.DB;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order implements Serializable {
    @Id
    @Generated
    private Long id;
//    FK
    private long userId;
//    FK
    private JsonNode items;
    private String status;
    private double totalAmount;
    private long deliveryOptionsId;
    private double deliveryCharge;
    private String couponApplied;
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String prescriptionUrl;
    private String transactionId;
    private Timestamp createdAt;
}
