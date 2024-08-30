package org.example.pharmaticb.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.BrandResponse;
import org.example.pharmaticb.Models.Response.CategoryResponse;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.brand.BrandService;
import org.example.pharmaticb.service.category.CategoryService;
import org.example.pharmaticb.service.country.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CountryService countryService;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        return productRepository.save(convertDtoToDb(request))
                .flatMap(product -> Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                        .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .flatMap(product -> {
                    log.info("Product Id: {}", product.getId());
                    return Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                            .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3()));
                });
    }

    @Override
    public Mono<ProductResponse> getProductById(long id) {
        return productRepository.findById(id)
                .flatMap(product -> Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                        .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
    }

    @Override
    public Mono<ProductResponse> updateProduct(long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    updateProductFromRequest(product, request);
                    return productRepository.save(product);
                })
                .flatMap(product -> Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                        .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        BeanUtils.copyProperties(request, product);
//        product.setProductName(request.getProductName());
//        product.setPrice(request.getPrice());
//        product.setImageUrl(request.getImageUrl());
//        product.setCategoryId(request.getCategoryId());
//        product.setDiscount(request.getDiscount());
//        product.setBrand(request.getBrand());
//        product.setExpires(request.getExpires());
//        product.setCountryOfOrigin(request.getCountryOfOrigin());
//        product.setDescription(request.getDescription());
//        product.setHowToUse(request.getHowToUse());
//        product.setIngredients(request.getIngredients());
//        product.setStock(request.getStock());
//        product.setCoupons(request.getCoupons());
    }

    @Override
    public Mono<Void> deleteProduct(long id) {
        return productRepository.deleteById(id);
    }

    @Override
    public Flux<ProductResponse> getProductsByCategoryId(long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .flatMap(product -> Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                        .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
    }

    private Mono<CountryResponse> getCountryResponse(long countryId) {
        return countryService.getCategoryById(countryId);
    }

    private ProductResponse convertDbToDto(Product product, CategoryResponse categoryResponse, BrandResponse brandResponse, CountryResponse countryResponse) {
        return ProductResponse.builder()
                .productId(String.valueOf(product.getId()))
                .productName(product.getProductName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(categoryResponse)
                .discount(product.getDiscount())
                .brand(brandResponse)
                .expires(product.getExpires())
                .country(countryResponse)
                .description(product.getDescription())
                .howToUse(product.getHowToUse())
                .ingredients(product.getIngredients())
                .stock(product.getStock())
                .coupons(product.getCoupons())
                .build();
    }

    private Mono<CategoryResponse> getCategoryResponse(long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    private Mono<BrandResponse> getBrandResponse(long brandId) {
        return brandService.getBrandById(brandId);
    }


    private Product convertDtoToDb(ProductRequest request) {
        return Product.builder()
                .productName(request.getProductName())
                .price(Double.parseDouble(request.getPrice()))
                .imageUrl(request.getImageUrl())
                .categoryId(Long.parseLong(request.getCategoryId()))
                .discount(Double.parseDouble(request.getDiscount()))
                .brandId(Long.parseLong(request.getBrandId()))
                .expires(request.getExpires())
                .countryId(Long.parseLong(request.getCountryId()))
                .description(request.getDescription())
                .howToUse(request.getHowToUse())
                .ingredients(request.getIngredients())
                .stock(Double.parseDouble(request.getStock()))
                .coupons(request.getCoupons())
                .build();
    }
}
