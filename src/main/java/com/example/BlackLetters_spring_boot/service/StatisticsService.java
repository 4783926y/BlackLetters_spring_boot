package com.example.BlackLetters_spring_boot.service;

import com.example.BlackLetters_spring_boot.domain.Budget;
import com.example.BlackLetters_spring_boot.persistence.BudgetRepository;
import com.example.BlackLetters_spring_boot.persistence.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ReceiptRepository receiptRepository;
    private final BudgetRepository budgetRepository;

    // 월별 카테고리별 지출 통계
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlyCategoryStats(Long userId, String yearMonth) {
        LocalDateTime startDate = LocalDate.parse(yearMonth + "-01").atStartOfDay();
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Object[]> rows = receiptRepository.findMonthlySpendingByCategory(userId, startDate, endDate);
        Long totalSpending = receiptRepository.findMonthlyTotalSpending(userId, startDate, endDate);

        List<Map<String, Object>> categories = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", row[0]);
            item.put("categoryName", row[1]);
            item.put("totalSpent", row[2]);
            categories.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("yearMonth", yearMonth);
        result.put("totalSpending", totalSpending != null ? totalSpending : 0);
        result.put("categories", categories);
        return result;
    }

    // 예산 대비 사용률
    @Transactional(readOnly = true)
    public Map<String, Object> getBudgetUsageStats(Long userId, String yearMonth) {
        LocalDate budgetMonth = LocalDate.parse(yearMonth + "-01");
        LocalDateTime startDate = budgetMonth.atStartOfDay();
        LocalDateTime endDate = startDate.plusMonths(1);

        // 해당 월 예산 목록
        List<Budget> budgets = budgetRepository.findByUserUserIdAndBudgetMonth(userId, budgetMonth);

        // 카테고리별 실제 지출
        List<Object[]> spendingRows = receiptRepository.findMonthlySpendingByCategory(userId, startDate, endDate);
        Map<Long, Integer> spendingMap = new HashMap<>();
        for (Object[] row : spendingRows) {
            spendingMap.put((Long) row[0], ((Number) row[2]).intValue());
        }

        List<Map<String, Object>> budgetUsages = new ArrayList<>();
        int totalBudget = 0;
        int totalSpent = 0;

        for (Budget budget : budgets) {
            Long categoryId = budget.getCategory().getCategoryId();
            int budgetAmount = budget.getAmount();
            int spentAmount = spendingMap.getOrDefault(categoryId, 0);
            double usageRate = budgetAmount > 0 ? (double) spentAmount / budgetAmount * 100 : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", categoryId);
            item.put("categoryName", budget.getCategory().getName());
            item.put("budgetAmount", budgetAmount);
            item.put("spentAmount", spentAmount);
            item.put("remainingAmount", budgetAmount - spentAmount);
            item.put("usageRate", Math.round(usageRate * 10) / 10.0); // 소수점 1자리

            budgetUsages.add(item);
            totalBudget += budgetAmount;
            totalSpent += spentAmount;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("yearMonth", yearMonth);
        result.put("totalBudget", totalBudget);
        result.put("totalSpent", totalSpent);
        result.put("totalRemaining", totalBudget - totalSpent);
        result.put("totalUsageRate", totalBudget > 0 ? Math.round((double) totalSpent / totalBudget * 1000) / 10.0 : 0);
        result.put("categories", budgetUsages);
        return result;
    }
}
