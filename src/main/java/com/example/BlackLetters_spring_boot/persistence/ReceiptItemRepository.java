package com.example.BlackLetters_spring_boot.persistence;

import com.example.BlackLetters_spring_boot.domain.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    List<ReceiptItem> findByReceiptReceiptId(Long receiptId);
    void deleteByReceiptReceiptId(Long receiptId);
}
