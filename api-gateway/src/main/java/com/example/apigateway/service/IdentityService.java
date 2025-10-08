package com.example.apigateway.service;

import com.example.apigateway.dto.request.IntrospectRequest;
import com.example.apigateway.dto.response.ApiResponse;
import com.example.apigateway.dto.response.IntrospectResponse;
import com.example.apigateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class IdentityService {

    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {

        return identityClient.introspect(IntrospectRequest.builder()
                .token(token)
                .build());
    }
}
