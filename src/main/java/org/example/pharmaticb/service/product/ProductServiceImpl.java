package org.example.pharmaticb.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        Product product = convertDtoToDb(request);
        return productRepository.insertProduct(product.getProductName(), product.getPrice(), product.getImageUrl(), product.getCategoryId(),
                product.getDiscount(), product.getBrand(), product.getExpires(), product.getCountryOfOrigin(),
                product.getDescription(), product.getHowToUse(),product.getIngredients(), product.getStock(), product.getCoupons())
                .map(id -> {
                    product.setId(id);
                    product.setNewProduct(true);
                    return product;
                })
                .map(product1 -> mapper.map(product1, ProductResponse.class));
//        return productRepository.save(product)
//                .map(product -> ProductResponse.builder().productId(String.valueOf(product.getId())).build());
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<ProductResponse> getProductById(long id) {
        return productRepository.findById(id)
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<ProductResponse> updateProduct(long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    updateProductFromRequest(product, request);
                    product.setNewProduct(false);
                    return productRepository.save(product);
                })
                .map(product1 -> mapper.map(product1, ProductResponse.class));
//        return productRepository.findById(id)
//                .flatMap(product -> {
//                    mapper.map(request, product);
//                    return productRepository.save(product)
//                            .map(product1 -> mapper.map(product1,ProductResponse.class));
//                });
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

    private ProductResponse convertDbToDto(Product product) {
        return mapper.map(product, ProductResponse.class);
//        return ProductResponse.builder()
//                .productId(String.valueOf(product.getId()))
//                .productName(product.getProductName())
//                .price(product.getPrice())
//                .imageUrl(product.getImageUrl())
//                .categoryId(product.getCategoryId())
//                .discount(product.getDiscount())
//                .brand(product.getBrand())
//                .expires(product.getExpires())
//                .countryOfOrigin(product.getCountryOfOrigin())
//                .description(product.getDescription())
//                .howToUse(product.getHowToUse())
//                .ingredients(product.getIngredients())
//                .stock(product.getStock())
//                .coupons(product.getCoupons())
//                .build();
    }

    private Product convertDtoToDb(ProductRequest request) {
        return mapper.map(request, Product.class);
//        return Product.builder()
//                .productName(request.getProductName())
//                .price(request.getPrice())
//                .categoryId(request.getCategoryId())
//                .discount(request.getDiscount())
//                .brand(request.getBrand())
//                .expires(request.getExpires())
//                .countryOfOrigin(request.getCountryOfOrigin())
//                .description(request.getDescription())
//                .howToUse(request.getHowToUse())
//                .ingredients(request.getIngredients())
//                .stock(request.getStock())
//                .coupons(request.getCoupons())
//                .build();
    }
}
