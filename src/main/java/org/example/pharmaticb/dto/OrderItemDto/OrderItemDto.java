package org.example.pharmaticb.dto.OrderItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto implements Serializable {
    private String productId;
    private String productName;
    private double unitPrice;
    private double quantity;
    private double totalPrice;
    private String remarks;
}
