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
    private String composition;
    private double price;
    private String imageUrl;
    private CategoryResponse category;
    private double discount;
    private BrandResponse brand;
    private String expires;
    private CountryResponse country;
    private String description;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;
}
