package com.cgzt.coinscode.transactions.adapters.outbound.entities;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private UserAccount source;
    @ManyToOne
    private UserAccount dest;
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
