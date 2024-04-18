package com.springlearning.global.security.handler.social;

import com.springlearning.global.redis.service.RefreshTokenService;
import com.springlearning.global.security.jwt.util.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class SocialLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Access Token 발급
        String accessToken = jwtProvider.createAccessToken(authentication);
        response.addHeader("Access-Token", "Bearer " + accessToken);

        // Refresh Token 발급
        String refreshToken = jwtProvider.createRefreshToken(authentication);
        Cookie cookie = createCookie(refreshToken);
        response.addCookie(cookie);

        // Refresh Token redis에 저장
        refreshTokenService.saveTokenInfo(refreshToken, accessToken);

        // 로그인 성공 메세지
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"로그인이 되었습니다.\"}");
    }

    public Cookie createCookie(String refreshToken) {
        String cookieName = "Refresh-Token";
        Cookie cookie = new Cookie(cookieName, refreshToken);
        // 쿠키 속성 설정
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(15 * 60 * 60 * 24);
        return cookie;
    }
}
