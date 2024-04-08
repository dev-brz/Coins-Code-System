package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models;

import java.util.List;

public record GetTransactionsResult(
        List<GetTransactionResult> transactions) {
}
