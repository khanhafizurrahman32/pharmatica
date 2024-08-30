package org.example.pharmaticb.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.dto.enums.Role;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderWithDetails implements Serializable {
    private Long id;
    //    FK
    private long userId;
    //    FK
    private JsonNode items;
    private String status;
    private double totalAmount;
    private double deliveryCharge;
    private String couponApplied;
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String transactionId;
    private Timestamp createdAt;
}
