package com.example.product_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),

    PRODUCT_NOT_FOUND(2001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS(2002, "Product already exists", HttpStatus.CONFLICT),
    PRODUCT_CREATE_FAILED(2003, "Failed to create product", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_UPDATE_FAILED(2004, "Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_DELETE_FAILED(2005, "Failed to delete product", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PRODUCT_DATA(2006, "Invalid product data", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTS(2007, "Product not exists", HttpStatus.CONFLICT),
    INTERNAL_ERROR(2008, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
