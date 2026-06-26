package com.example.BlackLetters_spring_boot.service;

import com.example.BlackLetters_spring_boot.domain.Category;
import com.example.BlackLetters_spring_boot.domain.User;
import com.example.BlackLetters_spring_boot.persistence.CategoryRepository;
import com.example.BlackLetters_spring_boot.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Category> getUserCategories(Long userId) {
        return categoryRepository.findGlobalAndUserCategories(userId);
    }

    @Transactional
    public Category createCustomCategory(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = Category.builder()
                .user(user)
                .name(name)
                .build();

        return categoryRepository.save(category);
    }
}
