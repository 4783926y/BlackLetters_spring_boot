package com.example.BlackLetters_spring_boot.controller;

import com.example.BlackLetters_spring_boot.domain.Receipt;
import com.example.BlackLetters_spring_boot.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<Receipt> uploadReceipt(
            @AuthenticationPrincipal Long userId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("file") MultipartFile file) throws Exception {
        
        Receipt receipt = receiptService.processReceiptAndSave(userId, categoryId, file);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping
    public ResponseEntity<List<Receipt>> getReceipts(@AuthenticationPrincipal Long userId) {
        List<Receipt> receipts = receiptService.getUserReceipts(userId);
        return ResponseEntity.ok(receipts);
    }
}
