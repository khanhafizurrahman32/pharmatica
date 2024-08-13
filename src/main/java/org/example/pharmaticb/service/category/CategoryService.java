package org.example.pharmaticb.service.category;

import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Response.CategoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<CategoryResponse> createCategory(CategoryRequest request);

    Flux<CategoryResponse>  getAllCategories();

    Mono<CategoryResponse> getCategoryById(Long id);

    Mono<CategoryResponse> updateCategory(long id, CategoryRequest request);

    Mono<Void> deleteCategory(long id);
}
