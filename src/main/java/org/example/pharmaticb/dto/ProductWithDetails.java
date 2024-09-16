package org.example.pharmaticb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithDetails {
    private Long id;
    private String productName;
    private double price;
    private String composition;
    private String imageUrl;
    private double discount;
    private String expires;
    private String description;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;

    private Long categoryId;
    private String categoryLabel;
    private String categoryIconUrl;
    private String categorySlug;
    private String[] subCategories;
    private String[] brand;
    private String[] priceRange;

    private Long brandId;
    private String brandName;

    private Long countryId;
    private String countryName;
}
