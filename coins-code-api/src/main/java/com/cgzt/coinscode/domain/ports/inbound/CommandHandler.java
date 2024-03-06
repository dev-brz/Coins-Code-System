package com.cgzt.coinscode.domain.ports.inbound;

public interface CommandHandler<C,R> {
    R handle(C command);
}
