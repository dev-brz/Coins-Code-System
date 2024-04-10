package com.cgzt.coinscode.transactions.domain.ports.inbound.commands;

import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.mappers.TransactionResultCodeMapper;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.models.TransactionCodeResult;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GenerateCodeCommandHandler {
    private final TransactionCodeRepository transactionCodeRepository;
    private final TransactionResultCodeMapper mapper;

    public TransactionCodeResult handle(Command command) {
        var code = transactionCodeRepository.generateTransactionCode(command.coinUid(), command.amount(), command.description());
        return mapper.map(code);
    }

    public record Command(
            @NotBlank String coinUid,
            @Min(0) BigDecimal amount,
            String description) {
    }
}
