package com.example.apigateway.configuration;


import com.example.apigateway.dto.response.ApiResponse;
import com.example.apigateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService  identityService;

    ObjectMapper objectMapper;

    @NonFinal
    private String[] pulicEndpoints = {
            "/identity/auth/.*",
            "/identity/users/registration",
            "/identity/auth/outbound/authentication",
            "/identity/auth/outbound/authentication",
            "/file/media/download/.*",
            "/post/post/getAllpost",
            "/profile/user_profile/allProfile",
            "/product/productInternal/getAllProduct"
    };

    @NonFinal
    @Value("${app.api-prefix}")
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter");

        if(isPulicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);

        if(CollectionUtils.isEmpty(authHeaders))
            return unauthentication(exchange.getResponse());

        String token = authHeaders.getFirst().replace("Bearer ", "");
        log.info("token is " + token);

        return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
            if(introspectResponseApiResponse.getResult().isValid())
                return  chain.filter(exchange);
            else
                return unauthentication(exchange.getResponse());
        }).onErrorResume(throwable -> unauthentication(exchange.getResponse()));

    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPulicEndpoint(ServerHttpRequest request) {
        return Arrays.stream(pulicEndpoints)
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }

    Mono<Void> unauthentication(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthorized")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return  response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
