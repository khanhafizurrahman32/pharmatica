package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.service.category.CategoryService;
import org.springframework.web.bind.annotation.RestController;

@BaseController
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
}
