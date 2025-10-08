package com.devteria.file.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponse {
    String id;
    String contentType;
    long size;
    String md5Checksum;
    String path;
    String url;
}
