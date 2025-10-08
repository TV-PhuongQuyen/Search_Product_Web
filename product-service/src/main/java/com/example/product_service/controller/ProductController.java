package com.example.product_service.controller;

import com.example.product_service.dto.ApiResponse;
import com.example.product_service.dto.request.ProductRequest;
import com.example.product_service.dto.response.PageResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/productInternal")
public class ProductController {

    ProductService productService;

    @GetMapping("/getAllProduct" )
    public ApiResponse<PageResponse<ProductResponse>> getAllPost(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "12") int size
    ){
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getProducts(page,size))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<ProductResponse> createProduct(
            @RequestPart("productRequest") @Valid ProductRequest  productRequest,
            @RequestPart("file") MultipartFile file   ) throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Product created successfully")
                .result(productService.createProduct(productRequest,file))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .code(200)
                        .message("Product deleted successfully")
                        .result(true)
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<String> searchProductByImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "k", defaultValue = "5") int k) {
        String result = productService.searchProductByImage(file, k);
        return ResponseEntity.ok(result);
    }
}
