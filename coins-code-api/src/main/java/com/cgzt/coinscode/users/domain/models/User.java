package com.cgzt.coinscode.users.domain.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private int numberOfSends;
    private int numberOfReceives;
    private LocalDateTime createdAt;
    private boolean active;
    private int sendLimits;
    private String imageName;
}
