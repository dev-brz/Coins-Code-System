package com.cgzt.coinscode.coins.domain.ports.inbound.queries;

import com.cgzt.coinscode.coins.domain.ports.inbound.queries.mappers.GetCoinsResultMapper;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.model.GetCoinsResult;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.CurrentUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCoinsQueryHandler {
    private final CoinsRepository coinsRepository;
    private final CurrentUserRepository currentUserRepository;
    private final GetCoinsResultMapper mapper;

    public GetCoinsResult handle(Query query) {
        if (StringUtils.isNotBlank(query.name()) && StringUtils.isNotBlank(query.username())) {
            var map = mapper.map(coinsRepository.findByUsernameAndName(query.name(), query.username()));
            return new GetCoinsResult(List.of(map));
        }

        if (StringUtils.isNotBlank(query.uid())) {
            var coin = coinsRepository.findByUid(query.uid());
            return new GetCoinsResult(List.of(mapper.map(coin)));
        }

        if (StringUtils.isNotBlank(query.username())) {
            return new GetCoinsResult(mapper.map(coinsRepository.findAllByUsername(query.username)));
        }

        return new GetCoinsResult(mapper.map(coinsRepository.findAll()));
    }

    public record Query(
            String uid,
            String username,
            String name
    ) {
    }
}
