package com.cgzt.coinscode.transactions.adapters.inbound;

import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.TopUpCommandHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.GetTransactionQueryHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.GetTransactionsQueryHandler;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.model.GetTransactionResult;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.model.GetTransactionsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "Transaction API")
class TransactionsController {
    public static final String TRANSACTIONS = "/transactions";
    public static final String TRANSACTIONS_NUMBER = "/transactions/{number}";
    public static final String TRANSACTIONS_TOPUP = "/transactions/topup";

    private final GetTransactionsQueryHandler getTransactionsQueryHandler;
    private final GetTransactionQueryHandler getTransactionQueryHandler;
    private final TopUpCommandHandler topUpCommandHandler;

    @GetMapping(TRANSACTIONS)
    @Operation(summary = "Get all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the transactions"),
            @ApiResponse(responseCode = "404", description = "Transactions not found", content = {}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {})
    })
    @PreAuthorize("hasRole('EMPLOYEE') or #username == authentication.name")
    GetTransactionsResult getAllTransactions(@RequestParam(required = false) String username, @RequestParam(required = false) String coinUid) {
        return getTransactionsQueryHandler.handle(new GetTransactionsQueryHandler.Query(username, coinUid));
    }

    @GetMapping(TRANSACTIONS_NUMBER)
    @Operation(summary = "Get a transaction by its number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = {}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {})
    })
    GetTransactionResult getTransactionByCode(@PathVariable String number) {
        return getTransactionQueryHandler.getTransactionByCode(number);
    }

    @PostMapping(TRANSACTIONS_TOPUP)
    @Operation(summary = "Top up a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top up successful"),
            @ApiResponse(responseCode = "400", description = "Invalid top up request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('EMPLOYEE') or #command.username == authentication.name")
    void topUp(@Valid @RequestBody TopUpCommandHandler.Command command) {
        topUpCommandHandler.handle(command);
    }
}
