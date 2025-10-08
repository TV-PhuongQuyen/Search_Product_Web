package com.example.category_service.controller;

import com.example.category_service.dto.ApiResponse;
import com.example.category_service.dto.request.CategoryRequest;
import com.example.category_service.dto.response.CategoryResponse;
import com.example.category_service.exception.AppException;
import com.example.category_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/categoryInternal")
public class CategoryController {

    CategoryService categoryService;

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        try {
            CategoryResponse categoryResponse = categoryService.getCategoryById(id);
            return ApiResponse.<CategoryResponse>builder()
                    .message("Successfully retrieved category")
                    .result(categoryResponse)
                    .build();
        } catch (AppException ex) {
            throw new RuntimeException();
        }
    }
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> createCategory(
            @RequestPart("categoryRequest") @Valid CategoryRequest categoryRequest,
            @RequestPart("file") MultipartFile logo) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Thêm thành công")
                .result(categoryService.createCategory(categoryRequest, logo))
                .build();
    }

}
