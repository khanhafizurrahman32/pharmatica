package org.example.pharmaticb.Models.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.utilities.Exception.ServiceError;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest implements Serializable {
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String productName;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private double price;
    private String imageUrl;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private long categoryId;
    private double discount;
    private String brand;
    private String expires;
    private String countryOfOrigin;
    private String description;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;
}
