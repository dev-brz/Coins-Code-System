package com.cgzt.coinscode.coins.domain.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Coin {
    private String uid;
    private String username;
    private String name;
    private String imageName;
    private String description;
    private BigDecimal amount;
}
