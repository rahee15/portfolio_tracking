package com.rahi.portfolio_tracking.model.response;

import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTransactionResponse {
    private TransactionType transactionType;

    private int quantity;

    private float unitPrice;

    private TickerType tickerType;

    private UUID transactionId;
}
