package com.cgzt.coinscode.transactions.adapters.outbound.entities;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_codes")
@Setter
@Getter
public class TransactionCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer code;
    @ManyToOne
    private CoinEntity coin;
    @OneToOne
    private UserAccountEntity owner;
    private BigDecimal amount;
    private LocalDateTime expiresAt;
    private String description;
}
