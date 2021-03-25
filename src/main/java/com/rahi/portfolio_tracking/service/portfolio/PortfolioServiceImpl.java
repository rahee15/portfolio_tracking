package com.rahi.portfolio_tracking.service.portfolio;

import com.rahi.portfolio_tracking.model.entity.SecurityDetails;
import com.rahi.portfolio_tracking.model.request.AddTransactionRequest;
import com.rahi.portfolio_tracking.type.TickerType;
import com.rahi.portfolio_tracking.type.TransactionType;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private HashMap<TickerType, SecurityDetails> securityAggregation = new HashMap<>();

    @Override
    public boolean validateTransaction(TransactionType transactionType, int quantity, TickerType tickerType) {
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

    @Override
    public HashMap<TickerType, SecurityDetails> getSecurityAggregation(){
        return securityAggregation;
    }

    @Override
    public void updatePortfolio(AddTransactionRequest addTransactionRequest) {
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

    @Override
    public float getReturn(){
        float returns = 0;
        for(TickerType key:securityAggregation.keySet()){
            returns += (securityAggregation.get(key).getAvgPrice() - 100)*securityAggregation.get(key).getQuantity();
        }
        return returns;
    }
}
