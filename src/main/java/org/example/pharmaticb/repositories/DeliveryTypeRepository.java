package org.example.pharmaticb.repositories;


import org.example.pharmaticb.Models.DB.DeliveryType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DeliveryTypeRepository extends R2dbcRepository<DeliveryType, Long> {
}
