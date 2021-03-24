package com.rahi.portfolio_tracking.controller;

import com.rahi.portfolio_tracking.exception.InvalidRequestException;
import com.rahi.portfolio_tracking.model.entity.SecurityDetails;
import com.rahi.portfolio_tracking.model.entity.Transaction;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.model.request.UpdateTransactionRequest;
import com.rahi.portfolio_tracking.model.response.AddTransactionResponse;
import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping()
public class TransactionController {

    private HashMap<TickerType, List<Transaction>> tickerTransactions = new HashMap<>();

    private HashMap<TickerType, SecurityDetails> securityAggregation = new HashMap<>();

    @GetMapping("/transactions")
    public ResponseEntity<HashMap<TickerType, List<Transaction>>> getAllTransaction() {
        return ResponseEntity.ok(tickerTransactions);
    }

    @DeleteMapping("/transactions/{tickerType}/{transactionId}")
    public void deleteTransaction(@PathVariable TickerType tickerType, @PathVariable UUID transactionId) {
        if (tickerTransactions.containsKey(tickerType)) {
            List<Transaction> transactions = tickerTransactions.get(tickerType);
            List<Transaction> trs = transactions.stream().filter(tr -> tr.getTransactionId().equals(transactionId)).collect(Collectors.toList());
            if (trs.size() > 0) {
                Transaction transaction = trs.get(0);
                TransactionType transactionType = TransactionType.BUY.equals(transaction.getTransactionType()) ? TransactionType.SELL : TransactionType.BUY;
                if (this.validateTransaction(transactionType, transaction.getQuantity(), tickerType)) {
                    this.updatePortfolio(AddTransactionRequest
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

            } else {
                throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No transaction found");
            }
        } else {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No transaction found");
        }

    }

    @GetMapping("/portfolio")
    public ResponseEntity<HashMap<TickerType, SecurityDetails>> getPortfolio() {
        return ResponseEntity.ok(securityAggregation);
    }

    @GetMapping("/returns")
    public ResponseEntity<Float> getReturns() {
        float returns = 0;
        for(TickerType key:securityAggregation.keySet()){
            returns += (securityAggregation.get(key).getAvgPrice() - 100)*securityAggregation.get(key).getQuantity();
        }
        return ResponseEntity.ok(returns);
    }

    @PostMapping("/transactions/update/{tickerType}/{transactionId}")
    public ResponseEntity<HashMap<TickerType, List<Transaction>>> UpdateTransaction(@PathVariable TickerType tickerType, @PathVariable UUID transactionId, @RequestBody UpdateTransactionRequest updateTransactionRequest) {
        Transaction transaction = this.getTransaction(transactionId, tickerType);
        this.deleteTransaction(tickerType,transactionId);
        Transaction updatedTransaction = new Transaction(transactionId,updateTransactionRequest.getTransactionType(),updateTransactionRequest.getQuantity(),updateTransactionRequest.getUnitPrice());
        tickerTransactions.get(tickerType).add(updatedTransaction);
        this.updatePortfolio(AddTransactionRequest.builder().unitPrice(updatedTransaction.getUnitPrice()).transactionType(updatedTransaction.getTransactionType()).tickerType(tickerType).quantity(updatedTransaction.getQuantity()).build());
        return ResponseEntity.ok(tickerTransactions);
    }

    private Transaction getTransaction(UUID transactionId, TickerType tickerType) {
        List<Transaction> transactions = tickerTransactions.get(tickerType).stream().filter(transaction -> transaction.getTransactionId().equals(transactionId)).collect(Collectors.toList());
        if (transactions.size() > 0) {
            return transactions.get(0);
        } else {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "No Transaction Found");
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<AddTransactionResponse> AddTransaction(@RequestBody AddTransactionRequest addTransactionRequest) {

        if (this.validateTransaction(addTransactionRequest.getTransactionType(), addTransactionRequest.getQuantity(), addTransactionRequest.getTickerType())) {
            Transaction newTransaction = new Transaction(
                    addTransactionRequest.getTransactionType(),
                    addTransactionRequest.getQuantity(),
                    addTransactionRequest.getUnitPrice());

            this.updateTransaction(addTransactionRequest, newTransaction);
            this.updatePortfolio(addTransactionRequest);

            return ResponseEntity.ok(AddTransactionResponse
                    .builder()
                    .transactionId(newTransaction.getTransactionId())
                    .quantity(newTransaction.getQuantity())
                    .transactionType(newTransaction.getTransactionType())
                    .unitPrice(newTransaction.getUnitPrice())
                    .tickerType(addTransactionRequest.getTickerType())
                    .build());
        } else {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid Quantity or Ticker");
        }

    }

    private boolean validateTransaction(TransactionType transactionType, int quantity, TickerType tickerType) {
        switch (transactionType) {
            case SELL:
                if (!securityAggregation.containsKey(tickerType) || (quantity
                        >
                        securityAggregation.get(tickerType).getQuantity()))
                    return false;
            case BUY:
                // Implement Portfolio Funds are enough or not
                return true;
            default:
                return false;
        }
    }

    private void updateTransaction(AddTransactionRequest addTransactionRequest, Transaction newTransaction) {

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

    private void updatePortfolio(AddTransactionRequest addTransactionRequest) {
        if (securityAggregation.containsKey(addTransactionRequest.getTickerType())) {
            int currentQuantity = securityAggregation.get(addTransactionRequest.getTickerType()).getQuantity();
            float currentPrice = securityAggregation.get(addTransactionRequest.getTickerType()).getAvgPrice();
            if (TransactionType.BUY.equals(addTransactionRequest.getTransactionType())) {
                currentPrice = (currentQuantity * currentPrice + addTransactionRequest.getQuantity() * addTransactionRequest.getUnitPrice()) / (currentQuantity + addTransactionRequest.getQuantity());
                currentQuantity = currentQuantity + addTransactionRequest.getQuantity();
            } else {
                if (currentQuantity != addTransactionRequest.getQuantity())
                    currentPrice = (currentQuantity * currentPrice - addTransactionRequest.getQuantity() * addTransactionRequest.getUnitPrice()) / (currentQuantity - addTransactionRequest.getQuantity());
                else
                    currentPrice = 0;
                currentQuantity = currentQuantity - addTransactionRequest.getQuantity();
            }
            securityAggregation.get(addTransactionRequest.getTickerType()).setQuantity(currentQuantity);
            securityAggregation.get(addTransactionRequest.getTickerType()).setAvgPrice(currentPrice);
        } else {
            securityAggregation.put(addTransactionRequest.getTickerType(), new SecurityDetails(addTransactionRequest.getQuantity(), addTransactionRequest.getUnitPrice()));
        }
    }
}
