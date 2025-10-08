package com.example.product_service.service;

import com.example.product_service.dto.request.ProductRequest;
import com.example.product_service.dto.response.PageResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.AppException;
import com.example.product_service.exception.ErrorCode;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.httpclient.CategoryClient;
import com.example.product_service.repository.httpclient.FileClient;
import com.example.product_service.repository.httpclient.OtoClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper  productMapper;
    OtoClient otoClient;
    FileClient fileClient;
    NavigationPythonService navigationPythonService;
    RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper;
    CategoryClient categoryClient;

    private final static String PRODUCT_KEY = "product_id:";

    public List<ProductResponse> cacheAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductResponse> responses = new ArrayList<>();

        for (Product product : products) {
            ProductResponse response = productMapper.toResponse(product);

            // Gọi category service
            var categoryResponse = categoryClient.getCategoryById(product.getCategoryId());
            if (categoryResponse.getResult() != null) {
                response.setNameCategory(categoryResponse.getResult().getName());
                response.setLogo(categoryResponse.getResult().getLogo());
            }

            // Lưu vào Redis
            String key = PRODUCT_KEY + product.getId();
            redisTemplate.opsForValue().set(key, response);

            responses.add(response);
        }
        System.out.println("Đã lưu " + products.size() + " sản phẩm vào Redis");
        return responses;
    }

    public PageResponse<ProductResponse> getProducts(int page, int size) {
        //Lấy toàn bộ key sản phẩm trong Redis (product:1, product:2, ...)
        Set<String> keys = redisTemplate.keys(PRODUCT_KEY+"*");

        List<ProductResponse> allProducts = new ArrayList<>();

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Object obj = redisTemplate.opsForValue().get(key);
                if (obj != null) {
                    // Dùng ObjectMapper inject vào, đã hỗ trợ LocalDateTime
                    ProductResponse product = objectMapper.convertValue(obj, ProductResponse.class);
                    allProducts.add(product);
                }
            }
            System.out.println("Lấy dữ liệu từ Redis (" + allProducts.size() + " sản phẩm)");
        } else {
            System.out.println(" Redis trống — tải dữ liệu từ DB...");
            allProducts = cacheAllProducts(); //vừa cache vừa lấy danh sách
        }
        allProducts.sort(Comparator.comparing(ProductResponse::getCreatedAt).reversed());

        int totalElements = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int fromIndex = Math.max(0, (page - 1) * size);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<ProductResponse> pagedProducts = new ArrayList<>();
        if (fromIndex < totalElements) {
            pagedProducts = allProducts.subList(fromIndex, toIndex);
        }

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .data(pagedProducts)
                .build();
    }

    public ProductResponse createProduct (ProductRequest request, MultipartFile file) {
       try{
           var authentication = SecurityContextHolder.getContext().getAuthentication();

           Long userId = otoClient.getUserId(authentication.getName());

           var responseUrl = fileClient.uploadMedia(file);

           Product  product = productMapper.toEntity(request);
           product.setCreatedAt(LocalDateTime.now());
           product.setUpdateAt(LocalDateTime.now());
           product.setImageUrl(responseUrl.getResult().getUrl());
           product.setUsersId(userId);

           productRepository.save(product);
           // Lưu vào Redis
           String redisKey = PRODUCT_KEY + product.getId();
           ProductResponse response = productMapper.toResponse(product);
           redisTemplate.opsForValue().set(redisKey, response);

           System.out.println("Đã thêm sản phẩm mới vào Redis với key = " + redisKey);
           String pythonService = navigationPythonService.sendVector(product.getId(), file,null);

           return productMapper.toResponse (product);
       }catch (Exception e){
           e.printStackTrace();
           throw  e;
       }
    }

    public String searchProductByImage(MultipartFile file, int k) {
        try {
            // Gọi service Python để tìm sản phẩm tương tự
            String result = navigationPythonService.searchImageProducts(file, k, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }



    @Transactional
    public boolean deleteProduct(Long id) {
        try {
            // Kiểm tra sản phẩm tồn tại
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));

            //  Soft delete: đánh dấu là đã xóa
            productRepository.deleteById(id);
            // Xóa vector khỏi Python service
            navigationPythonService.deleteVector(id, null);

            // Xóa cache Redis liên quan
            String redisKey = "product:" + id;
            if (redisTemplate.hasKey(redisKey)) {
                redisTemplate.delete(redisKey);
            }

            System.out.println("Sản phẩm " + id + " đã được soft delete và xóa cache Redis.");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}
