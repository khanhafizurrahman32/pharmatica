package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String userId;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
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
