package com.springlearning.global.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 15)
public class RefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    @Indexed
    private String accessToken;
}
