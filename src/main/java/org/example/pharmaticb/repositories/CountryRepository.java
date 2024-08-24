package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CountryRepository extends R2dbcRepository<Country, Long> {
}
