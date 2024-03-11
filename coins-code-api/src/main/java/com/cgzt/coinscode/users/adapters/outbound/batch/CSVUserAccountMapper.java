package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CSVUserAccountMapper {
    UserAccount csvToUserAccount(UserAccountProcessor.CSVUserAccount userAccount);

    default LocalDateTime map(String createdAt) {
        if ( StringUtils.isBlank(createdAt) ) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(StringUtils.replace(createdAt, " ", "T"));
    }
}
