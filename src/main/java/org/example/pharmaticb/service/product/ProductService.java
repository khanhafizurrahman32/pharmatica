package org.example.pharmaticb.service.product;

import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<ProductResponse> createProduct(ProductRequest request);

    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(long id);

    Mono<ProductResponse> updateProduct(long id, ProductRequest request);

    Mono<Void> deleteProduct(long id);

    Flux<ProductResponse> getProductsByCategoryId(long categoryId);
}
