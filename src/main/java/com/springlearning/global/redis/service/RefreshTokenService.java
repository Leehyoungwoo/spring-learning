package com.springlearning.global.redis.service;

import com.springlearning.global.redis.dao.RefreshTokenRepository;
import com.springlearning.global.redis.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(String username, String refreshToken, String accessToken) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .id(username)
                        .refreshToken(refreshToken)
                        .accessToken(accessToken)
                        .build()
        );
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
