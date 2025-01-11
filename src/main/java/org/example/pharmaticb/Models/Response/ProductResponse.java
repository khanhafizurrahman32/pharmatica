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
    private Category category;
    private double discount;
    private Brand brand;
    private String expires;
    private Country country;
    private String description;
    private String similarProducts;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;
}
