package com.cgzt.coinscode.core.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@UtilityClass
public class ResponseEntityUtils {
    public <T> ResponseEntity<T> created(String uid) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uid}")
                .buildAndExpand(uid)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
