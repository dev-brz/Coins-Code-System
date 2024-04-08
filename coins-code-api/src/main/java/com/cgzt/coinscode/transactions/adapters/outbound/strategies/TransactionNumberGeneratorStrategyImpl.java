package com.cgzt.coinscode.transactions.adapters.outbound.strategies;

import com.cgzt.coinscode.transactions.domain.ports.outbound.strategies.TransactionNumberGeneratorStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class TransactionNumberGeneratorStrategyImpl implements TransactionNumberGeneratorStrategy {
    @Value("${transaction.number.prefix.top-up}")
    private String topUpPrefix;

    @Override
    public String generate() {
        return "%s%d".formatted(topUpPrefix, System.currentTimeMillis());
    }
}
