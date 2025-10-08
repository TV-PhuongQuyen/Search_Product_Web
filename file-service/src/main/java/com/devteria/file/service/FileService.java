package com.devteria.file.service;

import com.devteria.file.dto.response.AvatarResponse;
import com.devteria.file.dto.response.FileData;
import com.devteria.file.dto.response.FileResponse;
import com.devteria.file.entity.FileMgmt;
import com.devteria.file.exception.AppException;
import com.devteria.file.exception.ErrorCode;
import com.devteria.file.mapper.FileMgmtMapper;
import com.devteria.file.repository.FileMgmtRepository;
import com.devteria.file.repository.FileRepository;
import com.devteria.file.repository.httpclient.OtoClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {

    FileRepository fileRepository;
    FileMgmtRepository  fileMgmtRepository;
    FileMgmtMapper fileMgmtMapper;
    OtoClient otoClient;

    public AvatarResponse uploadFile(MultipartFile file) throws IOException {
        String nameuser = SecurityContextHolder.getContext().getAuthentication().getName();

        Long idUser = otoClient.getUserId(nameuser);
        log.info("idUser:{}",idUser);


        var fileResponse = fileRepository.storeFileAvatar(file);

        var fileMgmt = fileMgmtMapper.toFileMgmt(fileResponse);
        fileMgmt.setOwnerId(idUser);
        fileMgmt = fileMgmtRepository.save(fileMgmt);

        return AvatarResponse.builder()
                .id(file.getOriginalFilename())
                .url(fileResponse.getUrl())
                .build();
    }

    public FileData downloadFile(String fileName) throws IOException {
        var fileMgmt = fileMgmtRepository.findById(fileName).orElseThrow(
                () -> new AppException(ErrorCode.FILE_NOT_FOUND)
        );
        var resource = fileRepository.read(fileMgmt);
        return new FileData(fileMgmt.getContentType(), resource);
    }


}
