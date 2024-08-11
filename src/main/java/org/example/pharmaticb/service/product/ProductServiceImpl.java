package org.example.pharmaticb.service.product;

import lombok.RequiredArgsConstructor;
import org.example.pharmaticb.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
}
