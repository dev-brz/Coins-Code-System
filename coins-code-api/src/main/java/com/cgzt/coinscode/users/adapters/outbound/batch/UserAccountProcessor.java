package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountProcessor implements ItemProcessor<UserAccountProcessor.CSVUserAccount, UserAccount> {
    private final CSVUserAccountMapper mapper;

    @Override
    public UserAccount process(CSVUserAccount csvUser) {
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
