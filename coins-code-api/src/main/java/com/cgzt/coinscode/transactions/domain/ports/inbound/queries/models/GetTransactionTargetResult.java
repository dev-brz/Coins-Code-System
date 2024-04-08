package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models;

public record GetTransactionTargetResult(
        String userUid,
        String coinUid,
        String firstName,
        String lastName,
        String username,
        String coinName) {
}
