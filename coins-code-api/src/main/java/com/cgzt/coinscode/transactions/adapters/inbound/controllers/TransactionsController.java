package com.cgzt.coinscode.transactions.adapters.inbound.controllers;

import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.GenerateCodeCommandHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.TopUpCommandHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.TransferCommandHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.models.TransactionCodeResult;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.GetTransactionCodeQueryHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.GetTransactionQueryHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.GetTransactionsQueryHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionCodeResult;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionResult;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(TransactionsController.TRANSACTIONS)
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "Transaction API")
class TransactionsController {
    public static final String TRANSACTIONS = "/transactions";
    public static final String NUMBER = "/{number}";
    public static final String TOPUP = "/top-up";
    public static final String CODES = "/codes";
    public static final String CODE = CODES + "/{code}";
    public static final String TRANSFER = "/transfer";

    private final GetTransactionsQueryHandler getTransactionsQueryHandler;
    private final GetTransactionQueryHandler getTransactionQueryHandler;
    private final TopUpCommandHandler topUpCommandHandler;
    private final GenerateCodeCommandHandler generateCodeCommandHandler;
    private final TransferCommandHandler transferCommandHandler;
    private final GetTransactionCodeQueryHandler getTransactionCodeQueryHandler;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or #username == authentication.name")
    @Operation(summary = "Get all transactions")
    @ApiResponse(responseCode = "200", description = "Found the transactions")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transactions not found", content = @Content)
    GetTransactionsResult getAllTransactions(@RequestParam(required = false) final String username,
                                             @RequestParam(required = false) final String coinUid) {
        return getTransactionsQueryHandler.handle(new GetTransactionsQueryHandler.Query(username, coinUid));
    }

    @GetMapping(NUMBER)
    @Operation(summary = "Get a transaction by its number")
    @ApiResponse(responseCode = "200", description = "Found the transaction")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    GetTransactionResult getTransactionByCode(@PathVariable final String number) {
        return getTransactionQueryHandler.getTransactionByCode(number);
    }

    @PostMapping(TOPUP)
    @PreAuthorize("hasRole('EMPLOYEE') or #command.username == authentication.name")
    @Operation(summary = "Top up a transaction")
    @ApiResponse(responseCode = "200", description = "Top up successful")
    @ApiResponse(responseCode = "400", description = "Invalid top up request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    void topUp(@Valid @RequestBody final TopUpCommandHandler.Command command) {
        topUpCommandHandler.handle(command);
    }

    @PostMapping(CODES)
    @Operation(summary = "Generate a new transaction code")
    @ApiResponse(responseCode = "200", description = "Code generated")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @PreAuthorize("hasRole('EMPLOYEE') or @coinRepository.validate(#command.coinUid , authentication.name)")
    TransactionCodeResult generateCode(@Valid @RequestBody GenerateCodeCommandHandler.Command command) {
        return generateCodeCommandHandler.handle(command);
    }

    @PostMapping(TRANSFER)
    @Operation(summary = "Transfer amount between coins using a transaction code")
    @ApiResponse(responseCode = "200", description = "Transfer successful")
    @ApiResponse(responseCode = "400", description = "Invalid transfer request", content = @Content)
    @PreAuthorize("hasRole('EMPLOYEE') or @coinRepository.validate(#command.coinUid , authentication.name)")
    void transfer(@Valid @RequestBody final TransferCommandHandler.Command command) {
        transferCommandHandler.handle(command);
    }

    @GetMapping(CODE)
    @Operation(summary = "Get a transaction code by its code")
    @ApiResponse(responseCode = "200", description = "Found the transaction code")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Transaction code not found", content = @Content)
    GetTransactionCodeResult getTransactionCode(@PathVariable final Integer code) {
        return getTransactionCodeQueryHandler.getTransactionCodeByCode(code);
    }
}
