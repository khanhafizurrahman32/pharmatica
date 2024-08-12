package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.service.product.ProductService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products")
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

    @PutMapping("/products/{id}")
    public Mono<ProductResponse> updateProduct(@Valid @PathVariable long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    public Mono<Void> deleteProduct(@Valid @PathVariable long id) {
        return productService.deleteProduct(id);
    }




}
