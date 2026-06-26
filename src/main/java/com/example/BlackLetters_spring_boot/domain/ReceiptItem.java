package com.example.BlackLetters_spring_boot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receipt_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice = 0;

    @Column(name = "amount", nullable = false)
    private Integer amount = 0;

    @Builder
    public ReceiptItem(Receipt receipt, String itemName, Integer quantity, Integer unitPrice, Integer amount) {
        this.receipt = receipt;
        this.itemName = itemName;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPrice = unitPrice != null ? unitPrice : 0;
        this.amount = amount != null ? amount : (this.quantity * this.unitPrice);
    }
}
