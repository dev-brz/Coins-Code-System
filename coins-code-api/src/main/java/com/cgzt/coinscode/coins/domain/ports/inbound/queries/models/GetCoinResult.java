package com.cgzt.coinscode.coins.domain.ports.inbound.queries.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public record GetCoinResult(
        String uid,
        @JsonIgnore String username,
        String name,
        String imageName,
        String description,
        BigDecimal amount
) {
}
