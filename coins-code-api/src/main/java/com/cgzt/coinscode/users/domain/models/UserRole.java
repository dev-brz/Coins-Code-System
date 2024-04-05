package com.cgzt.coinscode.users.domain.models;

import java.util.EnumSet;
import java.util.Set;

public enum UserRole {
    ADMIN, EMPLOYEE, USER, SENDER, RECEIVER;
    public final static Set<UserRole> CLIENT_ROLES = EnumSet.of(USER, SENDER, RECEIVER);
    public final static Set<UserRole> EMPLOYEE_ROLES = EnumSet.of(ADMIN, EMPLOYEE);
}
