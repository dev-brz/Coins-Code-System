package com.cgzt.coinscode.transactions.adapters.outbound.entities;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Setter
@Getter
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    @ManyToOne
    private UserAccountEntity source;
    @ManyToOne
    private UserAccountEntity dest;
    @OneToOne
    private CoinEntity sourceCoin;
    @OneToOne
    private CoinEntity destCoin;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(name = "t_type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(insertable = false)
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal amount;
}
