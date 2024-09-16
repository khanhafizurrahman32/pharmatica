package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Brand;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface BrandRepository extends R2dbcRepository<Brand, Long> {
    Mono<Brand> findByBrandName(String brandName);
}
