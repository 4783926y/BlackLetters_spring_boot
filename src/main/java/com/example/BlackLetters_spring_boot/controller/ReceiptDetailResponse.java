package com.example.BlackLetters_spring_boot.controller;

import com.example.BlackLetters_spring_boot.domain.OcrStatus;
import com.example.BlackLetters_spring_boot.domain.Receipt;
import com.example.BlackLetters_spring_boot.domain.ReceiptItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReceiptDetailResponse {

    private final Long receiptId;
    private final Long categoryId;
    private final String categoryName;
    private final String merchantName;
    private final LocalDateTime transactionDate;
    private final Integer totalAmount;
    private final String imageUrl;
    private final OcrStatus ocrStatus;
    private final String rawOcrText;
    private final LocalDateTime createdAt;
    private final List<ReceiptItemResponse> items;

    public ReceiptDetailResponse(Receipt receipt, String imageUrl, List<ReceiptItem> items) {
        this.receiptId = receipt.getReceiptId();
        this.categoryId = receipt.getCategory() != null ? receipt.getCategory().getCategoryId() : null;
        this.categoryName = receipt.getCategory() != null ? receipt.getCategory().getName() : null;
        this.merchantName = receipt.getMerchantName();
        this.transactionDate = receipt.getTransactionDate();
        this.totalAmount = receipt.getTotalAmount();
        this.imageUrl = imageUrl;
        this.ocrStatus = receipt.getOcrStatus();
        this.rawOcrText = receipt.getRawOcrText();
        this.createdAt = receipt.getCreatedAt();
        this.items = items.stream().map(ReceiptItemResponse::new).collect(Collectors.toList());
    }

    @Getter
    public static class ReceiptItemResponse {
        private final Long itemId;
        private final String itemName;
        private final Integer quantity;
        private final Integer unitPrice;
        private final Integer amount;

        public ReceiptItemResponse(ReceiptItem item) {
            this.itemId = item.getItemId();
            this.itemName = item.getItemName();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.amount = item.getAmount();
        }
    }
}
