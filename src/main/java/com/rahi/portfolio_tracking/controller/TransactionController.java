package com.rahi.portfolio_tracking.controller;

import com.rahi.portfolio_tracking.exception.InvalidRequestException;
import com.rahi.portfolio_tracking.model.entity.SecurityDetails;
import com.rahi.portfolio_tracking.model.entity.Transaction;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.model.request.UpdateTransactionRequest;
import com.rahi.portfolio_tracking.model.response.AddTransactionResponse;
import com.rahi.portfolio_tracking.service.portfolio.PortfolioService;
import com.rahi.portfolio_tracking.service.transaction.TransactionService;
import com.rahi.portfolio_tracking.type.TickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping()
public class TransactionController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionService transactionService;


    @GetMapping("/transactions")
    public ResponseEntity<HashMap<TickerType, List<Transaction>>> getAllTransaction() {
        return ResponseEntity.ok(this.transactionService.getTickerTransactions());
    }

    @DeleteMapping("/transactions/{tickerType}/{transactionId}")
    public void deleteTransaction(@PathVariable TickerType tickerType, @PathVariable UUID transactionId) {
        this.transactionService.deleteTransaction(tickerType, transactionId);

    }

    @GetMapping("/portfolio")
    public ResponseEntity<HashMap<TickerType, SecurityDetails>> getPortfolio() {
        return ResponseEntity.ok(this.portfolioService.getSecurityAggregation());
    }

    @GetMapping("/returns")
    public ResponseEntity<Float> getReturns() {
        return ResponseEntity.ok(this.portfolioService.getReturn());
    }

    @PostMapping("/transactions/update/{tickerType}/{transactionId}")
    public ResponseEntity<HashMap<TickerType, List<Transaction>>> UpdateTransaction(@PathVariable TickerType tickerType, @PathVariable UUID transactionId, @RequestBody UpdateTransactionRequest updateTransactionRequest) {
        Transaction updatedTransaction = this.transactionService.updateTransaction(tickerType, transactionId, updateTransactionRequest);
        this.portfolioService.updatePortfolio(AddTransactionRequest.builder().unitPrice(updatedTransaction.getUnitPrice()).transactionType(updatedTransaction.getTransactionType()).tickerType(tickerType).quantity(updatedTransaction.getQuantity()).build());
        return ResponseEntity.ok(this.transactionService.getTickerTransactions());
    }


    @PostMapping("/transactions")
    public ResponseEntity<AddTransactionResponse> AddTransaction(@RequestBody AddTransactionRequest addTransactionRequest) {

        if (this.portfolioService.validateTransaction(addTransactionRequest.getTransactionType(), addTransactionRequest.getQuantity(), addTransactionRequest.getTickerType())) {
            Transaction newTransaction = new Transaction(
                    addTransactionRequest.getTransactionType(),
                    addTransactionRequest.getQuantity(),
                    addTransactionRequest.getUnitPrice());

            this.transactionService.updateTransaction(addTransactionRequest, newTransaction);
            this.portfolioService.updatePortfolio(addTransactionRequest);

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


}
