package com.example.product_service.repository.httpclient;


import com.example.product_service.configuration.AuthenticationRequestInterceptor;
import com.example.product_service.dto.ApiResponse;
import com.example.product_service.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${app.services.file}", configuration = { AuthenticationRequestInterceptor.class })
public interface FileClient {
    @PostMapping(value = "/file/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadMedia(@RequestPart("file") MultipartFile file);
}
