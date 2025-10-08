package com.example.category_service.repository;

import com.example.category_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface categoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByNameCaseInsensitive(@Param("name") String name);

    boolean existsById(Long id);
}
