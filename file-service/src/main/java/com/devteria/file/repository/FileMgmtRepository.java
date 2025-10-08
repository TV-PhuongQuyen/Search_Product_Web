package com.devteria.file.repository;

import com.devteria.file.entity.FileMgmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMgmtRepository extends JpaRepository<FileMgmt,String> {
}
