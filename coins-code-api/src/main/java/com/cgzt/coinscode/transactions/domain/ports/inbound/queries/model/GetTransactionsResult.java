package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.model;

import java.util.List;

public record GetTransactionsResult(
        List<GetTransactionResult> transactions) {
}
