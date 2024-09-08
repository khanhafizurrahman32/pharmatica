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
    private String composition;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String price;
    private String imageUrl;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String categoryId;
    private String discount;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String brandId;
    private String expires;
    @NotBlank(message = ServiceError.INVALID_REQUEST)
    private String countryId;
    private String description;
    private String howToUse;
    private String ingredients;
    private String stock;
    private String[] coupons;
}
