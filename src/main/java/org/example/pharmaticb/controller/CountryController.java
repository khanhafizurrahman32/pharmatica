package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.CountryRequest;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.service.country.CountryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @PostMapping("/countries")
    public Mono<CountryResponse> createCountry(@Valid @RequestBody CountryRequest request) {
        return countryService.createCategory(request);
    }

    @GetMapping("/countries")
    public Flux<CountryResponse> getAllCountries() {
        return countryService.getAllCategories();
    }

    @GetMapping("/countries/{id}")
    public Mono<CountryResponse> getCountryById(@PathVariable Long id) {
        return countryService.getCategoryById(id);
    }

    @PutMapping("/countries/{id}")
    public Mono<CountryResponse> updateCountry(@Valid @PathVariable Long id, @Valid @RequestBody CountryRequest request) {
        return countryService.updateCategory(id, request);
    }

    @DeleteMapping("/countries/{id}")
    public Mono<Void> deleteCountry(@PathVariable Long id) {
        return countryService.deleteCategory(id);
    }
}
