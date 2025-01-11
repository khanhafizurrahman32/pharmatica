package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.dto.ProductWithDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Flux<Product> findAllBy(Pageable pageable);

    @Query("SELECT COUNT(*) FROM product WHERE category_id = :categoryId")
    Mono<Long> countProductsByCategoryId(Long categoryId);

    @Query("SELECT COUNT(*) FROM product WHERE brand_id = :brandId")
    Mono<Long> countProductsByBrandId(Long brandId);

    @Query("SELECT p.*, " +
            "c.id as category_id, c.label as category_label, c.icon_url as category_icon_url, " +
            "c.category_slug, c.sub_categories, c.brand, c.price_range, " +
            "b.id as brand_id, b.brand_name, " +
            "co.id as country_id, co.country_name " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "LEFT JOIN brand b ON p.brand_id = b.id " +
            "LEFT JOIN country co ON p.country_id = co.id " +
            "WHERE (:id IS NULL OR p.id = :id) " +
            "AND (CASE WHEN array_length(:ids, 1) IS NULL THEN true ELSE p.id = ANY(:ids) END) " +
            "AND (\n" +
            "        CASE\n" +
            "            WHEN array_length(:productNames, 1) IS NOT NULL THEN \n" +
            "                EXISTS (\n" +
            "                    SELECT 1 \n" +
            "                    FROM unnest(:productNames) name \n" +
            "                    WHERE LOWER(p.product_name) LIKE LOWER(CONCAT('%', name, '%'))\n" +
            "                )\n" +
            "            WHEN :productName IS NOT NULL THEN \n" +
            "                LOWER(p.product_name) LIKE LOWER(CONCAT('%', :productName, '%'))\n" +
            "            ELSE true\n" +
            "        END\n" +
            "    )" +
            "AND (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:brandId IS NULL OR p.brand_id = :brandId) " +
            "LIMIT :limit OFFSET :offset")
    Flux<ProductWithDetails> findAllProductDetails(Long id, Long[] ids, String productName, String[] productNames,
                                                   Long categoryId, Long brandId, int limit, Long offset);

    @Query("SELECT COUNT(*) " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "LEFT JOIN brand b ON p.brand_id = b.id " +
            "LEFT JOIN country co ON p.country_id = co.id " +
            "WHERE (:id IS NULL OR p.id = :id) " +
            "AND (:productName IS NULL OR p.product_name LIKE CONCAT('%', :productName, '%')) " +
            "AND (:categoryId IS NULL OR p.category_id = :categoryId) " +
            "AND (:brandId IS NULL OR p.brand_id = :brandId)")
    Mono<Long> countProducts(Long id, String productName, Long categoryId, Long brandId);
}
