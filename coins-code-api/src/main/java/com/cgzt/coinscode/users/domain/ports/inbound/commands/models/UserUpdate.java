package com.cgzt.coinscode.users.domain.ports.inbound.commands.models;

public record UserUpdate(
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
