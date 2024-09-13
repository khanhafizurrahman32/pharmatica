package org.example.pharmaticb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.dto.OrderItemDto.OrderItemDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptGenerationDto implements Serializable {
    private String companyLogo;
    private String barcodeLogo;
    private String billId;
    private String customerName;
    private String transactionDate;
    private String address;
    private String phoneNumber;
    private String email;
    private List<OrderItemDto> orderItems;
    private double totalPrice;
    private String trxId;
}
