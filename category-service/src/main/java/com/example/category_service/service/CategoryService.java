package com.example.category_service.service;

import com.example.category_service.dto.request.CategoryRequest;
import com.example.category_service.dto.response.CategoryResponse;
import com.example.category_service.entity.Category;
import com.example.category_service.exception.AppException;
import com.example.category_service.exception.ErrorCode;
import com.example.category_service.mapper.categoryMapper;
import com.example.category_service.repository.categoryRepository;
import com.example.category_service.repository.httpclient.FileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    categoryRepository  categoryRepository;

    categoryMapper categoryMapper;

    FileClient fileClient;

    public CategoryResponse createCategory(CategoryRequest categoryRequest, MultipartFile logo) {
        try {
            Category category = categoryMapper.toEntity(categoryRequest);

            if (categoryRepository.existsByNameCaseInsensitive(category.getName())) {
                throw new AppException(ErrorCode.CATEGORY_EXISTS);
            }
            var response = fileClient.uploadMedia(logo);
            category.setCreatedAt(LocalDateTime.now());
            category.setLogo(response.getResult().getUrl());
            return categoryMapper.toCategoryResponse(categoryRepository.save(category));
        } catch (AppException e) {
            log.error("Error creating category: {}", e.getMessage());
            throw e;
        }
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }


}
