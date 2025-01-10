package org.example.pharmaticb.Models.DB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product implements Serializable {
    @Id
    @Generated
    private Long id;
    private String productName;
    private double price;
    private String composition;
    private String imageUrl;
//    FK
    private long categoryId;
    private double discount;
//    FK
    private long brandId;
    private String expires;
//    FK
    private long countryId;
    private String similarProducts;
    private String description;
    private String howToUse;
    private String ingredients;
    private double stock;
    private String[] coupons;
}
