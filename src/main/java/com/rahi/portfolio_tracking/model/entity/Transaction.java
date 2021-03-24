package com.rahi.portfolio_tracking.model.entity;

import com.rahi.portfolio_tracking.type.TransactionType;
import com.rahi.portfolio_tracking.util.UUIDUtility;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Transaction {

    private UUID transactionId;

    private TransactionType transactionType;

    private int quantity;

    private float unitPrice;

    public Transaction() {
        this.transactionId = UUIDUtility.generate();
    }

    public Transaction(TransactionType transactionType, int quantity, float unitPrice) {
        this.transactionId = UUIDUtility.generate();
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
