package com.example.BlackLetters_spring_boot.service;

import com.example.BlackLetters_spring_boot.domain.Category;
import com.example.BlackLetters_spring_boot.domain.Receipt;
import com.example.BlackLetters_spring_boot.domain.ReceiptItem;
import com.example.BlackLetters_spring_boot.domain.User;
import com.example.BlackLetters_spring_boot.persistence.CategoryRepository;
import com.example.BlackLetters_spring_boot.persistence.ReceiptItemRepository;
import com.example.BlackLetters_spring_boot.persistence.ReceiptRepository;
import com.example.BlackLetters_spring_boot.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final S3UploadService s3UploadService;
    private final TextractService textractService;

    @Transactional
    public Receipt processReceiptAndSave(Long userId, Long categoryId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // 1. S3 업로드
        String imageUrl = s3UploadService.uploadFile(file);

        // 2. OCR 텍스트 추출
        Map<String, Object> extractedData = textractService.extractExpenseInfo(file);
        
        String merchantName = (String) extractedData.get("merchantName");
        Integer totalAmount = (Integer) extractedData.get("totalAmount");
        LocalDateTime receiptDate = (LocalDateTime) extractedData.get("receiptDate");
        String ocrStatus = (String) extractedData.get("ocrStatus");
        String rawOcrText = (String) extractedData.get("rawOcrText");

        // 3. 영수증 마스터 저장
        Receipt receipt = Receipt.builder()
                .user(user)
                .category(category)
                .merchantName(merchantName)
                .transactionDate(receiptDate)
                .totalAmount(totalAmount)
                .imageUrl(imageUrl)
                .ocrStatus(ocrStatus)
                .rawOcrText(rawOcrText)
                .build();
        
        receipt = receiptRepository.save(receipt);

        // 4. 품목 리스트 저장
        List<Map<String, Object>> items = (List<Map<String, Object>>) extractedData.get("items");
        if (items != null) {
            for (Map<String, Object> itemData : items) {
                String itemName = (String) itemData.get("itemName");
                Integer unitPrice = (Integer) itemData.get("unitPrice");
                Integer quantity = (Integer) itemData.get("quantity");
                
                ReceiptItem item = ReceiptItem.builder()
                        .receipt(receipt)
                        .itemName(itemName)
                        .unitPrice(unitPrice)
                        .quantity(quantity)
                        .build();
                receiptItemRepository.save(item);
            }
        }

        return receipt;
    }

    @Transactional(readOnly = true)
    public List<Receipt> getUserReceipts(Long userId) {
        return receiptRepository.findByUserUserIdOrderByTransactionDateDesc(userId);
    }
}
