package com.example.category_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    // Common
    ISBLANK(1008, "Không được trống dữ liệu", HttpStatus.BAD_REQUEST),
    INVALID_ID(1011, "ID không hợp lệ", HttpStatus.BAD_REQUEST),
    FORBIDDEN(1013, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),

    // Category
    CATEGORY_NOT_FOUND(1009, "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTS(1010, "Danh mục đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_IN_USE(1014, "Danh mục đang được sử dụng, không thể xóa", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_TOO_LONG(1015, "Tên danh mục quá dài", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_INVALID(1016, "Tên danh mục không hợp lệ", HttpStatus.BAD_REQUEST),

    // System
    DATABASE_ERROR(1999, "Lỗi kết nối cơ sở dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_ERROR(1998, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR);

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
