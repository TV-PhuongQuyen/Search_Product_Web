package com.example.product_service.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    BigDecimal price;
    String imageUrl;
    Long categoryId;
    Long usersId;
    LocalDateTime createdAt;

    String nameCategory;
    String logo;



}
