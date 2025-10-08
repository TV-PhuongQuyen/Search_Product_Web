package com.example.product_service.mapper;

import com.example.product_service.dto.request.ProductRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity (ProductRequest request);
    ProductResponse toResponse (Product entity);
}
