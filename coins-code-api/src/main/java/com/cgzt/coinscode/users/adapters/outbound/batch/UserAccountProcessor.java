package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountProcessor implements ItemProcessor<UserAccountProcessor.CSVUserAccount, UserAccountEntity> {
    private final CSVUserAccountMapper mapper;

    @Override
    public UserAccountEntity process(@NonNull CSVUserAccount csvUser) {
        return mapper.csvToUserAccount(csvUser);
    }

    public record CSVUserAccount(
            String username,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String imageName,
            int numberOfSends,
            int numberOfReceives,
            String createdAt,
            boolean active,
            int sendLimits
    ) {
    }
}
