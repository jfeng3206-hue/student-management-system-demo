package com.example.demo.service;

import com.example.demo.dto.ProductDto;

public interface ProductService {
    ProductDto getById(Long id);
    ProductDto create(ProductDto productDto);
    ProductDto update(Long id, ProductDto productDto);
    void delete(Long id);
}
