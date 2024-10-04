package org.example.pharmaticb.service.brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Brand;
import org.example.pharmaticb.Models.Request.BrandRequest;
import org.example.pharmaticb.Models.Response.BrandResponse;
import org.example.pharmaticb.repositories.BrandRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.utilities.log.Loggable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    @Loggable
    public Mono<BrandResponse> createBrand(BrandRequest request) {
        return brandRepository.save(modelMapper.map(request, Brand.class))
                .map(brand -> modelMapper.map(brand, BrandResponse.class));
    }

    @Override
    @Loggable
    public Flux<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .flatMap(brand -> productRepository.countProductsByBrandId(brand.getId())
                        .map(count -> convertDbToDto(brand, count)));
    }

    @Override
    @Loggable
    public Mono<BrandResponse> getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(brand -> modelMapper.map(brand, BrandResponse.class));
    }

    @Override
    @Loggable
    public Mono<BrandResponse> updateBrand(Long id, BrandRequest request) {
        return brandRepository.findById(id)
                .flatMap(brand -> {
                    modelMapper.map(request, brand);
                    return brandRepository.save(brand)
                            .map(updateBrand -> modelMapper.map(updateBrand, BrandResponse.class));
                });
    }

    @Override
    @Loggable
    public Mono<Void> deleteBrand(Long id) {
        return brandRepository.deleteById(id);
    }

    private BrandResponse convertDbToDto(Brand brand, Long count) {
        return BrandResponse.builder()
                .id(String.valueOf(brand.getId()))
                .brandName(brand.getBrandName())
                .totalProductCount(String.valueOf(count))
                .build();

    }
}
