package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
}
