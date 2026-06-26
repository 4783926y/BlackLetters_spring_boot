package com.example.BlackLetters_spring_boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TextractService {

    private final TextractClient textractClient;

    public Map<String, Object> extractExpenseInfo(MultipartFile file) throws IOException {
        Map<String, Object> extractedData = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        extractedData.put("items", items);
        
        try {
            Document document = Document.builder()
                    .bytes(SdkBytes.fromInputStream(file.getInputStream()))
                    .build();

            AnalyzeExpenseRequest request = AnalyzeExpenseRequest.builder()
                    .document(document)
                    .build();

            AnalyzeExpenseResponse response = textractClient.analyzeExpense(request);
            
            // rawOcrText 보관을 위해 전체 응답 저장
            extractedData.put("rawOcrText", response.toString());
            extractedData.put("ocrStatus", "COMPLETED");
            
            for (ExpenseDocument expenseDoc : response.expenseDocuments()) {
                // 상호명 및 총액 파싱
                for (ExpenseField field : expenseDoc.summaryFields()) {
                    String type = field.type().text();
                    String value = field.valueDetection().text();

                    if ("VENDOR_NAME".equals(type)) {
                        extractedData.put("merchantName", value);
                    } else if ("TOTAL".equals(type)) {
                        String numberOnly = value.replaceAll("[^0-9]", "");
                        if (!numberOnly.isEmpty()) {
                            extractedData.put("totalAmount", Integer.parseInt(numberOnly));
                        }
                    }
                }
                
                // 품목 리스트 파싱
                for (LineItemGroup lineItemGroup : expenseDoc.lineItemGroups()) {
                    for (LineItemFields lineItem : lineItemGroup.lineItems()) {
                        Map<String, Object> itemData = new HashMap<>();
                        for (ExpenseField field : lineItem.lineItemExpenseFields()) {
                            String type = field.type().text();
                            String value = field.valueDetection().text();
                            
                            if ("ITEM".equals(type)) {
                                itemData.put("itemName", value);
                            } else if ("PRICE".equals(type)) {
                                String numberOnly = value.replaceAll("[^0-9]", "");
                                if (!numberOnly.isEmpty()) {
                                    itemData.put("unitPrice", Integer.parseInt(numberOnly));
                                }
                            } else if ("QUANTITY".equals(type)) {
                                String numberOnly = value.replaceAll("[^0-9]", "");
                                if (!numberOnly.isEmpty()) {
                                    itemData.put("quantity", Integer.parseInt(numberOnly));
                                }
                            }
                        }
                        // 이름이 추출된 품목만 추가
                        if (itemData.containsKey("itemName")) {
                            items.add(itemData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // AWS 미설정 또는 오류 시 FAILED 및 더미 데이터 처리
            extractedData.put("ocrStatus", "FAILED");
            extractedData.put("rawOcrText", e.getMessage());
            extractedData.put("merchantName", "더미 상호명(AWS 연결안됨)");
            extractedData.put("totalAmount", 15000);
            extractedData.put("receiptDate", LocalDateTime.now());
            
            Map<String, Object> dummyItem = new HashMap<>();
            dummyItem.put("itemName", "더미 상품");
            dummyItem.put("unitPrice", 15000);
            dummyItem.put("quantity", 1);
            items.add(dummyItem);
        }

        if (!extractedData.containsKey("receiptDate")) {
            extractedData.put("receiptDate", LocalDateTime.now());
        }
        
        return extractedData;
    }
}
