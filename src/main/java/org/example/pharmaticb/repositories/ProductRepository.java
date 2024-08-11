package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
}
