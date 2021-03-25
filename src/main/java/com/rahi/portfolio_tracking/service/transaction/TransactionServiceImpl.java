package com.rahi.portfolio_tracking.service.transaction;

import com.rahi.portfolio_tracking.exception.InvalidRequestException;
import com.rahi.portfolio_tracking.model.entity.Transaction;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.model.request.UpdateTransactionRequest;
import com.rahi.portfolio_tracking.service.portfolio.PortfolioService;
import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private PortfolioService portfolioService;

    private HashMap<TickerType, List<Transaction>> tickerTransactions = new HashMap<>();

    @Override
    public void updateTransaction(AddTransactionRequest addTransactionRequest, Transaction newTransaction) {

        if (tickerTransactions.containsKey(addTransactionRequest.getTickerType())) {
            //Existing Transaction For Given Ticker
            tickerTransactions.get(addTransactionRequest.getTickerType()).add(newTransaction);

        } else {
            // First Transaction for the given Ticker
            List<Transaction> newTickerTransaction = new ArrayList<>();
            newTickerTransaction.add(newTransaction);
            tickerTransactions.put(addTransactionRequest.getTickerType(), newTickerTransaction);
        }
    }

    @Override
    public HashMap<TickerType, List<Transaction>> getTickerTransactions(){
        return tickerTransactions;
    }

    @Override
    public HashMap<TickerType, List<Transaction>> deleteTransaction(TickerType tickerType, UUID transactionId){
        HashMap<TickerType, List<Transaction>> tickerTransactions = this.getTickerTransactions();
        if (tickerTransactions.containsKey(tickerType)) {
            List<Transaction> transactions = tickerTransactions.get(tickerType);
            List<Transaction> trs = transactions.stream().filter(tr -> tr.getTransactionId().equals(transactionId)).collect(Collectors.toList());
            if (trs.size() > 0) {
                Transaction transaction = trs.get(0);
                TransactionType transactionType = TransactionType.BUY.equals(transaction.getTransactionType()) ? TransactionType.SELL : TransactionType.BUY;
                if (this.portfolioService.validateTransaction(transactionType, transaction.getQuantity(), tickerType)) {
                    this.portfolioService.updatePortfolio(AddTransactionRequest
                            .builder()
                            .quantity(transaction.getQuantity())
                            .tickerType(tickerType)
                            .transactionType(transactionType)
                            .unitPrice(transaction.getUnitPrice())
                            .build());
                } else {
                    throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Not valid delete request");
                }
                tickerTransactions.put(tickerType, tickerTransactions.get(tickerType).stream().filter(tr -> !tr.getTransactionId().equals(transactionId)).collect(Collectors.toList()));
                return tickerTransactions;
            } else {
                throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No transaction found");
            }
        } else {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No transaction found");
        }
    }

    @Override
    public Transaction updateTransaction(TickerType tickerType, UUID transactionId, UpdateTransactionRequest updateTransactionRequest){
        Transaction transaction = this.getTransaction(transactionId, tickerType);
        this.deleteTransaction(tickerType, transactionId);
        Transaction updatedTransaction = new Transaction(transactionId, updateTransactionRequest.getTransactionType(), updateTransactionRequest.getQuantity(), updateTransactionRequest.getUnitPrice());
        tickerTransactions.get(tickerType).add(updatedTransaction);
        return updatedTransaction;
    }

    @Override
    public Transaction getTransaction(UUID transactionId, TickerType tickerType) {
        List<Transaction> transactions = tickerTransactions.get(tickerType).stream().filter(transaction -> transaction.getTransactionId().equals(transactionId)).collect(Collectors.toList());
        if (transactions.size() > 0) {
            return transactions.get(0);
        } else {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No Transaction Found");
        }
    }
}
