package org.example.pharmaticb.Models.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pharmaticb.Models.DB.Brand;
import org.example.pharmaticb.Models.DB.Category;
import org.example.pharmaticb.Models.DB.Country;

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
    private Category categoryInfo;
    private double discount;
    private BrandResponse brand;
    private Brand brandInfo;
    private String expires;
    private CountryResponse country;
    private Country countryInfo;
    private String description;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;
}
