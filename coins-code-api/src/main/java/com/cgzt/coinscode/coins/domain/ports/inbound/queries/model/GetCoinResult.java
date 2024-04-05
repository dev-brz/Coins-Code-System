package com.cgzt.coinscode.coins.domain.ports.inbound.queries.model;

import java.math.BigDecimal;

public record GetCoinResult(
        String uid,
        String name,
        String imageName,
        String description,
        BigDecimal amount
) {
}
