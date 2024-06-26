package com.springlearning.global.redis.service;

import com.springlearning.global.redis.dao.RefreshTokenRepository;
import com.springlearning.global.redis.entity.RefreshToken;
import com.springlearning.global.security.jwt.dto.TokenDto;
import com.springlearning.global.security.jwt.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(String refreshToken, String accessToken) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .accessToken(accessToken)
                        .build()
        );
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        log.info("redis에서 삭제");
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    public RefreshToken findRefreshTokenByAccessToken(String accessToken) {
        return refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token cookie expired"));
    }

    @Transactional
    public TokenDto refreshAccessToken(String accessToken, String refreshToken) {
        RefreshToken refreshTokenByAccessToken = this.findRefreshTokenByAccessToken(accessToken);
        log.info("리프레시 레디스에서 가져오기" + refreshTokenByAccessToken.getRefreshToken());
        if (!refreshToken.equals(refreshTokenByAccessToken.getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Refresh Token");
        }

        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtProvider.createAccessToken(authentication);
        this.removeRefreshToken(accessToken);
        this.saveTokenInfo(refreshToken, newAccessToken);
        return TokenDto.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
