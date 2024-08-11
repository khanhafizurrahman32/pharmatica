package org.example.pharmaticb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.example.pharmaticb.service.product.ProductService;

@BaseController
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


}
