package com.devteria.file.mapper;

import com.devteria.file.dto.response.FileResponse;
import com.devteria.file.entity.FileMgmt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMgmtMapper {
    FileMgmt toFileMgmt(FileResponse response);
}
