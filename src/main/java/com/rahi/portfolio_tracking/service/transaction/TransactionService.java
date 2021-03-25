package com.rahi.portfolio_tracking.service.transaction;

import com.rahi.portfolio_tracking.model.entity.Transaction;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.model.request.UpdateTransactionRequest;
import com.rahi.portfolio_tracking.type.TickerType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    void updateTransaction(AddTransactionRequest addTransactionRequest, Transaction newTransaction);

    HashMap<TickerType, List<Transaction>> getTickerTransactions();

    HashMap<TickerType, List<Transaction>> deleteTransaction(TickerType tickerType, UUID transactionId);

    Transaction updateTransaction(TickerType tickerType, UUID transactionId, UpdateTransactionRequest updateTransactionRequest);

    Transaction getTransaction(UUID transactionId, TickerType tickerType);
}
