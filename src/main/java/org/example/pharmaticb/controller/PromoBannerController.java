package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.PromoBannerRequest;
import org.example.pharmaticb.Models.Response.PromoBannerResponse;
import org.example.pharmaticb.service.banner.PromoBannerService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class PromoBannerController {
    private final PromoBannerService promoBannerService;

    @PostMapping("/banners/create")
    public Mono<PromoBannerResponse> createBanner(@Valid @RequestBody PromoBannerRequest request) {
        return promoBannerService.createBanner(request);
    }

    @GetMapping("/banners")
    public Flux<PromoBannerResponse> getAllBanners() {
        return promoBannerService.getAllBanners();
    }

    @GetMapping("/banners/{id}")
    public Mono<PromoBannerResponse> getBannerById(@Valid @PathVariable Long id) {
        return promoBannerService.getBannerById(id);
    }

    @PutMapping("/banners/{id}")
    public Mono<PromoBannerResponse> updateBanner(@Valid @PathVariable Long id, @Valid @RequestBody PromoBannerRequest request) {
        return promoBannerService.updateBanner(id, request);
    }

    @DeleteMapping("/banners/{id}")
    public Mono<Void> deleteBanner(@Valid @PathVariable Long id) {
        return promoBannerService.deleteBanner(id);
    }
}
