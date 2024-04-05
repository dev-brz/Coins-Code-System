package com.cgzt.coinscode.coins.adapters.outbound.entities;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "coins")
@Setter
@Getter
@NoArgsConstructor
public class CoinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(insertable = false)
    private String uid;
    @ManyToOne
    private UserAccount userAccount;
    private String name;
    private String imageName;
    private String description;
    private BigDecimal amount;
}
