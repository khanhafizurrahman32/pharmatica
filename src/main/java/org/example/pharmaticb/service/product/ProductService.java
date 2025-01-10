package org.example.pharmaticb.service.product;

import org.example.pharmaticb.Models.Request.BulkProductCreateRequest;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.BulkProductCreateResponse;
import org.example.pharmaticb.Models.Response.OrderResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

public interface ProductService {
    Mono<ProductResponse> createProduct(ProductRequest request);

    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(long id);

    Mono<ProductResponse> updateProduct(long id, ProductRequest request);

    Mono<Void> deleteProduct(long id);

    Flux<ProductResponse> getProductsByCategoryId(long categoryId);

    Flux<ProductResponse> getProductsByBrandId(long brandId);

    Flux<ProductResponse> getProductsByProductName(String productName);

    Mono<BulkProductCreateResponse> createBulkProduct(@Valid BulkProductCreateRequest request);

    Mono<PagedResponse<ProductResponse>> getPageProducts(int page, int size, String sortBy, String sortDirection);

    Flux<ProductResponse> getSimilarProductById(long id);
}
