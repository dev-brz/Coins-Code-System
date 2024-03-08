package com.cgzt.coinscode.users.domain.ports.inbound.queries.model;

import java.util.List;

public record GetUsersResult(List<GetUserResult> users) {
}
