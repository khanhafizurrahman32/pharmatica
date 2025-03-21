package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateDeliveryChargeRequest implements Serializable {
    private String orderId;
    //Todo: Enum validation
    private String deliveryCharge;
}
