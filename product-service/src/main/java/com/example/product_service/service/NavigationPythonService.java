package com.example.product_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NavigationPythonService {
    RestTemplate restTemplate = new RestTemplate();
    String defaultPythonApi = "http://127.0.0.1:8000";

    private MultiValueMap<String, Object> buildMultipart(MultipartFile file, Long productId) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Gói file
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileResource);

        // Gói product_id kiểu String để FastAPI nhận integer
        if (productId != null) {
            body.add("product_id", productId.toString());
        }

        return body;
    }

    // Hàm 2: Gửi request POST tới Python
    private String postToPython(String url, MultiValueMap<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi Python service: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Hàm chính sendVector
    public String sendVector(Long productId, MultipartFile file, String pythonApi) {
        try {
            // Gói file + product_id
            MultiValueMap<String, Object> body = buildMultipart(file, productId);

            // URL Python API
            String url = (pythonApi != null && !pythonApi.isBlank())
                    ? pythonApi
                    : defaultPythonApi + "/encode";

            // Gửi request
            return postToPython(url, body);

        } catch (Exception e) {
            System.err.println("Lỗi sendVector: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String searchImageProducts(MultipartFile file,int k, String pythonApi) {
        try {
            MultiValueMap<String, Object> body = buildMultipart(file, null); // Không gửi product_id
            String url = (pythonApi != null && !pythonApi.isBlank())
                    ? pythonApi + "/search_image_product?k=" + k
                    : defaultPythonApi + "/search_image_product?k=" + k;
            return postToPython(url, body);
        } catch (Exception e) {
            System.err.println("Lỗi searchImageProducts: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Gọi API xóa (soft delete) bên Python
    public String deleteVector(Long productId, String pythonApi) {
        try {
            String url = (pythonApi != null && !pythonApi.isBlank()) ? pythonApi : defaultPythonApi + "/delete_product/" + productId;
            restTemplate.delete(url);
            return "Deleted product " + productId + " in FAISS (soft delete)";
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi API delete Python service: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}

