package com.cgzt.coinscode.users.domain.ports.inbound.queries.models;

import java.util.List;

public record GetUsersResult(List<GetUserResult> users) {
}
