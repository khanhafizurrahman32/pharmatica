package org.example.pharmaticb.service.category;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.Models.DB.Category;
import org.example.pharmaticb.Models.Request.CategoryRequest;
import org.example.pharmaticb.Models.Response.CategoryResponse;
import org.example.pharmaticb.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<CategoryResponse> createCategory(CategoryRequest request) {
        return categoryRepository.save(mapper.map(request, Category.class))
                .map(category -> mapper.map(category, CategoryResponse.class));
    }

    @Override
    public Flux<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .map(category -> mapper.map(category, CategoryResponse.class));
    }

    @Override
    public Mono<CategoryResponse> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> mapper.map(category, CategoryResponse.class));
    }

    @Override
    public Mono<CategoryResponse> updateCategory(long id, CategoryRequest request) {
        return categoryRepository.findById(id)
                .flatMap(category -> {
                    mapper.map(request, category);
                    return categoryRepository.save(category)
                            .map(c -> mapper.map(c, CategoryResponse.class));
                });
    }

    @Override
    public Mono<Void> deleteCategory(long id) {
        return categoryRepository.deleteById(id);
    }


}
