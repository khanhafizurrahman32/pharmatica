package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Category;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CategoryRepository extends R2dbcRepository<Category, Long> {
}
