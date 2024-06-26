package com.cgzt.coinscode.users.domain.ports.inbound.queries.models;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetUserResult(
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        int numberOfSends,
        int numberOfReceives,
        LocalDateTime createdAt,
        boolean active,
        BigDecimal sendLimits,
        BigDecimal currentSendLimits,
        String imageName
) {
}
