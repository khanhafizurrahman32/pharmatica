package org.example.pharmaticb.Models.DB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product implements Serializable {
    @Id
    private Long id;
    private String productName;
    private double price;
    private String imageUrl;
//    FK
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

    @Transient
    private boolean newProduct = true;

    public boolean isNew() {
        return this.newProduct || id == null;
    }
}
