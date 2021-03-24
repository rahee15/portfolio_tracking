package com.rahi.portfolio_tracking.model.request;

import com.rahi.portfolio_tracking.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionRequest {

    private TransactionType transactionType;

    private int quantity;

    private float unitPrice;

}
