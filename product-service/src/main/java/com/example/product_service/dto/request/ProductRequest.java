package com.example.product_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be less than 255 characters")
    String name;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price;
    @NotNull(message = "Category ID is required")
    Long categoryId;
}
