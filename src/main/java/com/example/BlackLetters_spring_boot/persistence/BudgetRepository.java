package com.example.BlackLetters_spring_boot.persistence;

import com.example.BlackLetters_spring_boot.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserUserIdAndBudgetMonth(Long userId, LocalDate budgetMonth);
}
