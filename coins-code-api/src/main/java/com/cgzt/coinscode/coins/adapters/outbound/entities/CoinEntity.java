package com.cgzt.coinscode.coins.adapters.outbound.entities;

import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "coins")
@Setter
@Getter
public class CoinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(insertable = false)
    private String uid;
    @ManyToOne
    private UserAccountEntity userAccount;
    private String name;
    private String imageName;
    private String description;
    private BigDecimal amount;
    @OneToMany(mappedBy = "coin", fetch = FetchType.EAGER)
    @SQLRestriction("expires_at > CURRENT_TIMESTAMP")
    private List<TransactionCodeEntity> transactionCodes;
}
