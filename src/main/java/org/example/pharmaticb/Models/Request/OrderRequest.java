package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.dto.records.Item;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private List<Item> items;
    private String deliveryOptionId;
    private String couponApplied;
    private String paymentChannel;
    private String prescriptionUrl;
    private String deliveryCharge;
}
