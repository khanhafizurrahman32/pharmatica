package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Response.CategoryResponse;
import org.example.pharmaticb.service.category.CategoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@BaseController
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public Mono<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/categories")
    public Flux<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/categories/{id}")
    public Mono<CategoryResponse> getCategoryById(@Valid @PathVariable long id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/categories/{id}")
    public Mono<CategoryResponse> updateCategory(@Valid @PathVariable long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public Mono<Void> deleteCategory(@Valid @PathVariable long id) {
        return categoryService.deleteCategory(id);
    }
}
