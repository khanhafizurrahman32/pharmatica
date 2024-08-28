package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.BrandRequest;
import org.example.pharmaticb.Models.Response.BrandResponse;
import org.example.pharmaticb.service.brand.BrandService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping("/brands")
    public Mono<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        return brandService.createBrand(request);
    }

    @GetMapping("/brands")
    public Flux<BrandResponse> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/brands/{id}")
    public Mono<BrandResponse> getBrandById(@PathVariable Long id) {
        return brandService.getBrandById(id);
    }

    @PutMapping("/brands/{id}")
    public Mono<BrandResponse> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return brandService.updateBrand(id, request);
    }

    @DeleteMapping("/brands/{id}")
    public Mono<Void> deleteBrand(@PathVariable Long id) {
        return brandService.deleteBrand(id);
    }
}
