package com.example.BlackLetters_spring_boot.persistence;

import com.example.BlackLetters_spring_boot.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 글로벌 카테고리(user IS NULL)와 해당 유저의 커스텀 카테고리를 함께 조회
    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.userId = :userId")
    List<Category> findGlobalAndUserCategories(@Param("userId") Long userId);
}
