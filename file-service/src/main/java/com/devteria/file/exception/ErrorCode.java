package com.devteria.file.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1003, "User not found",HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed",HttpStatus.NOT_FOUND),
    UNAUTHENTICATION(1006, "Unauthentication",HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1008, "Token không hợp lệ hoặc đã hết hạn",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1009, "ROlE không tồn tại",HttpStatus.NOT_FOUND),
    HET_HAN_TOKEN(1010, "Token da het han", HttpStatus.FORBIDDEN),
    FILE_NOT_FOUND(404, "Khong tim thay file", HttpStatus.FORBIDDEN),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
    ErrorCode() {
    }

    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
