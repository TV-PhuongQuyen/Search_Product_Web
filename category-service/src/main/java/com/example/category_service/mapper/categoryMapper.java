package com.example.category_service.mapper;

import com.example.category_service.dto.request.CategoryRequest;
import com.example.category_service.dto.response.CategoryResponse;
import com.example.category_service.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface categoryMapper {
    Category toEntity(CategoryRequest categoryRequest);
    CategoryResponse toCategoryResponse (Category category);
}
