package com.cgzt.coinscode.coins.domain.ports.inbound.queries.models;

import java.util.List;

public record GetCoinsResult(
        List<GetCoinResult> coins
) {
}
