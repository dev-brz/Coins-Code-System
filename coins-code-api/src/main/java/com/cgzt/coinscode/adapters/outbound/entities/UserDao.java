package com.cgzt.coinscode.adapters.outbound.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "user_account")
@Data
public class UserDao {
    @Id
    @GeneratedValue
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
