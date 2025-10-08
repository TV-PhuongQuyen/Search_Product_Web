package com.devteria.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileMgmt {
    @Id
    String id;
    Long ownerId;
    String contentType;
    long size;
    @Column(name = "md5_checksum")
    String md5Checksum;
    String path;
}

