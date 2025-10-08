package com.example.product_service.repository.httpclient;


import com.example.product_service.configuration.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "oto-service", url = "${app.services.oto}", configuration = { AuthenticationRequestInterceptor.class })
public interface OtoClient {
    @GetMapping(value = "/users/id", produces = MediaType.APPLICATION_JSON_VALUE)
    Long getUserId(@RequestParam("username") String username);
}
