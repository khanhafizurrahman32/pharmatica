package org.example.pharmaticb.repositories;

import org.example.pharmaticb.Models.DB.PromoBanner;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductBannerRepository  extends R2dbcRepository<PromoBanner, Long> {
}
