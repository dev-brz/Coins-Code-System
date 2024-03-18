package com.cgzt.coinscode.coins.domain.ports.inbound.queries.model;

import java.util.List;

public record GetCoinsResult(
        List<GetCoinResult> coins

) {
}
