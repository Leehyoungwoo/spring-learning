package com.springlearning.global.security.jwt.api;

import com.springlearning.global.redis.service.RefreshTokenService;
import com.springlearning.global.security.jwt.dto.TokenDto;
import com.springlearning.global.security.jwt.util.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AutoController {

    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    public void refreshAccessToken(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @Valid @RequestBody TokenDto tokenDto) {
        log.info("access token 재발급 시작");
        String refreshToken = this.getCookie(request).getValue();
        log.info("refresh : " + refreshToken);
        String accessToken = tokenDto.accessToken();
        log.info("acceess : " + accessToken);
        TokenDto newTokenDto = refreshTokenService.refreshAccessToken(accessToken, refreshToken);
        response.addHeader("Access-Token", "Bearer " + newTokenDto.accessToken());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Redis에 있는 refresh token 제거
        String accessToken = jwtProvider.resolveToken(request);
        refreshTokenService.removeRefreshToken(accessToken);
        Cookie refreshCookie = getCookie(request);
        if (refreshCookie != null) {
            refreshCookie.setMaxAge(0);
            response.addCookie(refreshCookie);
        }
    }

    private Cookie getCookie(HttpServletRequest request) {
        log.info(Objects.toString(request) + "쿠키");
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("Refresh-Token"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token cookie expired"));
    }
}
