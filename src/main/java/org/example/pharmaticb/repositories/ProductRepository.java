package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
    @Query("INSERT INTO product (product_name, price, image_url, category_id, discount, brand, expires, country_of_origin, description, how_to_use, ingredients, stock, coupons) " +
            "VALUES (:productName, :price, :imageUrl, :categoryId, :discount, :brand, :expires, :countryOfOrigin, :description, :howToUse, :ingredients, :stock, :coupons) " +
            "RETURNING id")
    Mono<Long> insertProduct(String productName, double price, String imageUrl, long categoryId, double discount, String brand, String expires,
                             String countryOfOrigin, String description, String howToUse, String ingredients, double stock, String[] coupons);

}
