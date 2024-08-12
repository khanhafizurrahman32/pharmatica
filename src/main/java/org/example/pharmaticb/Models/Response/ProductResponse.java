package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private String productId;
    private String productName;
    private double price;
    private String imageUrl;
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
