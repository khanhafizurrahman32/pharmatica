package org.example.pharmaticb.service.banner;

import org.example.pharmaticb.Models.Request.PromoBannerRequest;
import org.example.pharmaticb.Models.Response.PromoBannerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface PromoBannerService {

    Mono<PromoBannerResponse> createBanner(PromoBannerRequest request);

    Flux<PromoBannerResponse> getAllBanners();

    Mono<PromoBannerResponse> getBannerById(@Valid Long id);

    Mono<PromoBannerResponse> updateBanner(@Valid Long id, @Valid PromoBannerRequest request);

    Mono<Void> deleteBanner(@Valid Long id);
}
