package org.example.pharmaticb.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Brand;
import org.example.pharmaticb.Models.DB.Category;
import org.example.pharmaticb.Models.DB.Country;
import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.Models.Request.BulkProductCreateRequest;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.*;
import org.example.pharmaticb.repositories.BrandRepository;
import org.example.pharmaticb.repositories.CategoryRepository;
import org.example.pharmaticb.repositories.CountryRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.brand.BrandService;
import org.example.pharmaticb.service.category.CategoryService;
import org.example.pharmaticb.service.country.CountryService;
import org.example.pharmaticb.service.file.FileUploadService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CountryService countryService;
    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        return productRepository.save(convertDtoToDb(request, Product.builder().build()))
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
                    var productUpdated = convertDtoToDb(request, product);
                    return productRepository.save(productUpdated);
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

    @Override
    public Flux<ProductResponse> getProductsByBrandId(long brandId) {
        return productRepository.findByBrandId(brandId)
                .flatMap(product -> Mono.zip(getCategoryResponse(product.getCategoryId()), getBrandResponse(product.getBrandId()), getCountryResponse(product.getCountryId()))
                        .map(tuple3 -> convertDbToDto(product, tuple3.getT1(), tuple3.getT2(), tuple3.getT3())));
    }

    @Override
    public Mono<BulkProductCreateResponse> createBulkProduct(BulkProductCreateRequest request) {
        return fileUploadService.downloadFile(request.getFilePath())
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .flatMapMany(content -> Flux.fromStream(
                        new BufferedReader(new InputStreamReader(
                                new ByteArrayInputStream(content.getBytes())))
                                .lines()
                                .skip(1)
                ))
                .map(this::parseProductFromCsvLine)
                .flatMap(productMono -> productMono)
                .flatMap(this::insertProductIntoDatabase)
                .collectList()
                .map(ret ->BulkProductCreateResponse.builder().success(true).build());
    }

    private Mono<Product> insertProductIntoDatabase(Product product) {
        return productRepository.save(product);
    }

    private Mono<Product> parseProductFromCsvLine(String line) {
        String[] parts = line.split(",");
        return Mono.zip(getCategoryId(parts[3]), getBrandId(parts[5]), getCountryId(parts[7]))
                .map(tuple3 -> Product.builder()
                        .productName(parts[0])
                        .price(Double.parseDouble(parts[1]))
                        .imageUrl(parts[2])
                        .categoryId(tuple3.getT1())
                        .discount(Double.parseDouble(parts[4]))
                        .brandId(tuple3.getT2())
                        .expires(parts[6])
                        .countryId(tuple3.getT3())
                        .description(parts[8])
                        .howToUse(parts[9])
                        .ingredients(parts[10])
                        .stock(Double.parseDouble(parts[11]))
                        .composition(parts[12])
                        .build());
    }

    private Mono<Long> getCategoryId(String label) {
        return categoryRepository.findByLabel(label)
                .map(Category::getId);
    }

    private Mono<Long> getBrandId(String label) {
        return brandRepository.findByBrandName(label)
                .map(Brand::getId);
    }

    private Mono<Long> getCountryId(String label) {
        return countryRepository.findByCountryName(label)
                .map(Country::getId);
    }

    private Mono<CountryResponse> getCountryResponse(long countryId) {
        return countryService.getCategoryById(countryId);
    }

    private ProductResponse convertDbToDto(Product product, CategoryResponse categoryResponse, BrandResponse brandResponse, CountryResponse countryResponse) {
        return ProductResponse.builder()
                .productId(String.valueOf(product.getId()))
                .productName(product.getProductName())
                .price(product.getPrice())
                .composition(product.getComposition())
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


    private Product convertDtoToDb(ProductRequest request, Product product) {
        return Product.builder()
                .id(!ObjectUtils.isEmpty(product.getId()) ? product.getId() : null)
                .productName(request.getProductName())
                .composition(request.getComposition())
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
