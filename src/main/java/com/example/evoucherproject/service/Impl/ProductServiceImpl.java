package com.example.evoucherproject.service.Impl;

import com.example.evoucherproject.exception.CustomException;
import com.example.evoucherproject.mapper.DataMapper;
import com.example.evoucherproject.model.dto.CustomResponse;
import com.example.evoucherproject.model.dto.request.customer.CreateCustomerDto;
import com.example.evoucherproject.model.dto.request.product.CreateProductDto;
import com.example.evoucherproject.model.entity.Customer;
import com.example.evoucherproject.model.entity.Product;

import com.example.evoucherproject.repository.ProductRepository;
import com.example.evoucherproject.service.ProductService;
import com.example.evoucherproject.ultil.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private  ProductRepository productRepository;

    @Override
    public CustomResponse getByidProduct(Integer id) {
        Optional<Product> exitsProduct = productRepository.findById(id);
        if (!exitsProduct.isPresent()) {
            throw new CustomException("Product ko tim thay!", HttpStatus.NOT_FOUND);
        }

        return new CustomResponse(" Product with id: " + id + "đã được tìm thấy!",
                HttpStatus.OK, exitsProduct);
    }

    @Override
    public CustomResponse createProduct(CreateProductDto dto, BindingResult result) {
        if (result.hasErrors()) {
            throw new CustomException(ValidationUtils.getValidationErrorString(result), HttpStatus.BAD_REQUEST);
        }
        Product product = DataMapper.toEntity(dto, Product.class);
        return new CustomResponse("Product created successfully!",
                HttpStatus.CREATED, productRepository.save(product));
    }
}
