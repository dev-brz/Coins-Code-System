package com.cgzt.coinscode.coins.domain.models;

import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Coin {
    private String uid;
    private String username;
    private String name;
    private String imageName;
    private String description;
    private BigDecimal amount;
    private List<TransactionCode> transactionCodes;
}
