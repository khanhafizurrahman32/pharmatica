package org.example.pharmaticb.service.banner;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.PromoBanner;
import org.example.pharmaticb.Models.Request.PromoBannerRequest;
import org.example.pharmaticb.Models.Response.PromoBannerResponse;
import org.example.pharmaticb.repositories.ProductBannerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PromoBannerServiceImpl implements PromoBannerService {
    private final ProductBannerRepository productBannerRepository;
    private final ModelMapper mapper;


    @Override
    public Mono<PromoBannerResponse> createBanner(PromoBannerRequest request) {
        return productBannerRepository.save(mapper.map(request, PromoBanner.class))
                .map(banner -> mapper.map(banner, PromoBannerResponse.class));
    }

    @Override
    public Flux<PromoBannerResponse> getAllBanners() {
        return productBannerRepository.findAll()
                .map(promoBanner -> mapper.map(promoBanner, PromoBannerResponse.class));
    }

    @Override
    public Mono<PromoBannerResponse> getBannerById(Long id) {
        return productBannerRepository.findById(id)
                .map(promoBanner -> mapper.map(promoBanner, PromoBannerResponse.class));
    }

    @Override
    public Mono<PromoBannerResponse> updateBanner(Long id, PromoBannerRequest request) {
        return productBannerRepository.findById(id)
                .flatMap(banner -> {
                    mapper.map(request, banner);
                    return productBannerRepository.save(banner)
                            .map(promoBanner -> mapper.map(promoBanner, PromoBannerResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteBanner(Long id) {
        return productBannerRepository.deleteById(id);
    }
}
