package com.cgzt.coinscode.users.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Getter
@Setter
public class UserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String imageName;
    private int numberOfSends;
    private int numberOfReceives;
    private LocalDateTime createdAt;
    private boolean active;
    private int sendLimits;
}
