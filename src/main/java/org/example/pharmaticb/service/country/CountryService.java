package org.example.pharmaticb.service.country;

import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Response.CategoryResponse;
import org.example.pharmaticb.Models.Response.CountryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {
    Mono<CountryResponse> createCategory(CountryRequest request);

    Flux<CountryResponse> getAllCategories();

    Mono<CountryResponse> getCategoryById(Long id);

    Mono<CountryResponse> updateCategory(long id, CountryRequest request);

    Mono<Void> deleteCategory(long id);
}
