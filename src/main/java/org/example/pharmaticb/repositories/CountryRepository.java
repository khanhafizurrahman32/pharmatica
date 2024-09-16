package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends R2dbcRepository<Country, Long> {
    Mono<Country> findByCountryName(String countryName);
}
