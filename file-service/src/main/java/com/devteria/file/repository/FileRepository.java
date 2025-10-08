package com.devteria.file.repository;

import com.devteria.file.dto.response.FileResponse;
import com.devteria.file.entity.FileMgmt;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Configuration
public class FileRepository {
    @NonFinal
    @Value("${app.file.storage-avtar}")
    protected  String fileStorage;

    @NonFinal
    @Value("${app.file.download-prefix}")
    protected String urlPrefix;

    public FileResponse storeFileAvatar(MultipartFile file) throws IOException {
        Path  folder = Paths.get(fileStorage);
        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fileExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        return  FileResponse.builder()
                .id(fileName)
                .size(file.getSize())
                .contentType(file.getContentType())
                .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .path(filePath.toString())
                .url(urlPrefix + fileName)
                .build();
    }

    public Resource read(FileMgmt fileMgmt)  throws IOException {
        var data = Files.readAllBytes(Paths.get(fileMgmt.getPath()));
        return new ByteArrayResource(data);

    }
}
