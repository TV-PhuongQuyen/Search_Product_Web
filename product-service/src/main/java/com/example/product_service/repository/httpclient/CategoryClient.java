package com.example.product_service.repository.httpclient;

import com.example.product_service.configuration.AuthenticationRequestInterceptor;
import com.example.product_service.dto.ApiResponse;
import com.example.product_service.dto.response.CategoryResponse;
import com.example.product_service.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "category-service", url = "${app.services.category}", configuration = { AuthenticationRequestInterceptor.class })
public interface CategoryClient {
    @GetMapping("/category/categoryInternal/{id}")
    ApiResponse<CategoryResponse> getCategoryById(@PathVariable("id") Long id);
}
