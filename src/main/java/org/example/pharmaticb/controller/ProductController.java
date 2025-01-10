package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.Request.BulkProductCreateRequest;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.BulkProductCreateResponse;
import org.example.pharmaticb.Models.Response.PagedResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.service.product.ProductService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@BaseController
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products/create")
    public Mono<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/products")
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Mono<ProductResponse> getProductById(@Valid @PathVariable long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/products/{id}/similar")
    public Flux<ProductResponse> getSimilarProductById(@Valid @PathVariable long id) {
        return productService.getSimilarProductById(id);
    }

    @GetMapping("/products/name/{productName}")
    public Flux<ProductResponse> getProductsByProductName(@Valid @PathVariable String productName) {
        return productService.getProductsByProductName(productName);
    }

    @GetMapping("/products/paginated")
    public Mono<PagedResponse<ProductResponse>> getProductsPaginated(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "id") String sortBy,
                                                                 @RequestParam(defaultValue = "ASC") String sortDirection) {
        return productService.getPageProducts(page, size, sortBy, sortDirection);
    }

    @PutMapping("/products/{id}")
    public Mono<ProductResponse> updateProduct(@Valid @PathVariable long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    public Mono<Void> deleteProduct(@Valid @PathVariable long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/products/category/{categoryId}")
    public Flux<ProductResponse> getProductsByCategoryId(@PathVariable long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/products/brand/{brandId}")
    public Flux<ProductResponse> getProductsByBrandId(@PathVariable long brandId) {
        return productService.getProductsByBrandId(brandId);
    }

    @PostMapping("/products/bulk-create")
    public Mono<BulkProductCreateResponse> createBulkProduct(@Valid @RequestBody BulkProductCreateRequest request) {
        return productService.createBulkProduct(request);
    }
}
