package com.rahi.portfolio_tracking.service.portfolio;

import com.rahi.portfolio_tracking.model.entity.SecurityDetails;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;

import java.util.HashMap;

public interface PortfolioService {
    boolean validateTransaction(TransactionType transactionType, int quantity, TickerType tickerType);

    HashMap<TickerType, SecurityDetails> getSecurityAggregation();

    void updatePortfolio(AddTransactionRequest addTransactionRequest);

    float getReturn();
}
