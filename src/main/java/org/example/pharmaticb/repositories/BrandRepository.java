package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Brand;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface BrandRepository extends R2dbcRepository<Brand, Long> {
}
