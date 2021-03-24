package com.rahi.portfolio_tracking.model.request;

import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddTransactionRequest {

    private TransactionType transactionType;

    private int quantity;

    private float unitPrice;

    private TickerType tickerType;

}
