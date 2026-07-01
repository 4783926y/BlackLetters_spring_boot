package com.example.BlackLetters_spring_boot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GeminiOcrServiceTest {

    private GeminiOcrService geminiOcrService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        geminiOcrService = new GeminiOcrService();
    }

    @Test
    void testFallbackWhenApiKeyNotConfigured() {
        // Given
        ReflectionTestUtils.setField(geminiOcrService, "apiKey", null);
        ReflectionTestUtils.setField(geminiOcrService, "apiUrl", "http://localhost");
        MockMultipartFile file = new MockMultipartFile("file", "receipt.jpg", "image/jpeg", "dummy data".getBytes());

        // When
        Map<String, Object> result = geminiOcrService.extractExpenseInfo(file);

        // Then
        assertEquals("FAILED", result.get("ocrStatus"));
        assertEquals("더미 상호명(Gemini 연결안됨)", result.get("merchantName"));
        assertEquals(15000, result.get("totalAmount"));
        assertNotNull(result.get("receiptDate"));

        var items = (java.util.List<?>) result.get("items");
        assertNotNull(items);
        assertEquals(1, items.size());

        Map<?, ?> item = (Map<?, ?>) items.get(0);
        assertEquals("더미 상품", item.get("itemName"));
        assertEquals(15000, item.get("unitPrice"));
        assertEquals(1, item.get("quantity"));
    }

    @Test
    void testDateParsing() {
        LocalDateTime parsed = (LocalDateTime) ReflectionTestUtils.invokeMethod(geminiOcrService, "parseReceiptDate", "2026-07-01");
        assertNotNull(parsed);
        assertEquals(2026, parsed.getYear());
        assertEquals(7, parsed.getMonthValue());
        assertEquals(1, parsed.getDayOfMonth());
        assertEquals(0, parsed.getHour());

        LocalDateTime parsedFull = (LocalDateTime) ReflectionTestUtils.invokeMethod(geminiOcrService, "parseReceiptDate", "2026-07-01T12:30:45");
        assertNotNull(parsedFull);
        assertEquals(12, parsedFull.getHour());
        assertEquals(30, parsedFull.getMinute());
        assertEquals(45, parsedFull.getSecond());
    }
}
