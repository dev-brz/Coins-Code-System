package com.cgzt.coinscode.transactions.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionTarget {
    private String coinUid;
    private String coinName;
    private String username;
    private String firstName;
    private String lastName;
}
