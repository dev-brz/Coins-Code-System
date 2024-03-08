package com.cgzt.coinscode.users.domain.ports.inbound.commands.model;

public record UserUpdate(
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
