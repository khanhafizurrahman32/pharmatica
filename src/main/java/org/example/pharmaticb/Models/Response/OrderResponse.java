package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.dto.OrderItemDto.OrderItemDto;
import org.example.pharmaticb.dto.UserDto;
import org.example.pharmaticb.dto.records.Item;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse implements Serializable {
    @Builder.Default
    private UserDto user = UserDto.builder().build();
    private String id;
    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();
    private String status;
    private double totalAmount;
    private double deliveryCharge;
    private String couponApplied;
    private LocalDate deliveryDate;
    private String paymentChannel;
    private String transactionId;
    private String orderDate;
}
