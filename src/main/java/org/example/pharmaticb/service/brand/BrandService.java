package org.example.pharmaticb.service.brand;

import org.example.pharmaticb.Models.Request.BrandRequest;
import org.example.pharmaticb.Models.Response.BrandResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface BrandService {
    Mono<BrandResponse> createBrand(@Valid BrandRequest request);

    Flux<BrandResponse> getAllBrands();

    Mono<BrandResponse> getBrandById(Long id);

    Mono<BrandResponse> updateBrand(Long id, @Valid BrandRequest request);

    Mono<Void> deleteBrand(Long id);
}
