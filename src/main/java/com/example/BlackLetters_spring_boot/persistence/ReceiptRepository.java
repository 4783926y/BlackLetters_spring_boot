package com.example.BlackLetters_spring_boot.persistence;

import com.example.BlackLetters_spring_boot.domain.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByUserUserIdOrderByTransactionDateDesc(Long userId);

    // 특정 월의 카테고리별 지출 합계
    @Query("SELECT r.category.categoryId, r.category.name, SUM(r.totalAmount) " +
           "FROM Receipt r " +
           "WHERE r.user.userId = :userId " +
           "AND r.transactionDate >= :startDate " +
           "AND r.transactionDate < :endDate " +
           "GROUP BY r.category.categoryId, r.category.name")
    List<Object[]> findMonthlySpendingByCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 특정 월 전체 지출 합계
    @Query("SELECT SUM(r.totalAmount) FROM Receipt r " +
           "WHERE r.user.userId = :userId " +
           "AND r.transactionDate >= :startDate " +
           "AND r.transactionDate < :endDate")
    Long findMonthlyTotalSpending(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
