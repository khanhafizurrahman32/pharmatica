package org.example.pharmaticb.service.category;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

}
