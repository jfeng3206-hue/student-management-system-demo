package com.example.demo.service.impl;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.Student;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;


    @Override
    public ProductDto getById(Long id){
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return ProductMapper.mapToProductDto(product);
    }

    @Override
    public ProductDto create(ProductDto productDto){
        Product product = ProductMapper.mapToProduct(productDto);
        Product saved = repository.save(product);
        return ProductMapper.mapToProductDto(saved);
    }

    @Override
    public ProductDto update(Long id, ProductDto productDto){
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setName(productDto.getName());
        product.setPrice(product.getPrice());
        product.setStock(product.getStock());
        Product updated = repository.save(product);
        return ProductMapper.mapToProductDto(updated);
    }

    @Override
    public void delete(Long id){
        Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        repository.deleteById(id);

    }



}
