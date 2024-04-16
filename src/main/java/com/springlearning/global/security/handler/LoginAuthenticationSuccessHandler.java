package com.springlearning.global.security.handler;

import com.springlearning.domain.member.application.MemberService;
import com.springlearning.global.security.jwt.util.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인 성공 시 실패 회수 초기화
        String username = request.getParameter("username");
        memberService.resetLoginFailCount(username);

        // Access Token 발급
        String accessToken = jwtProvider.createAccessToken(authentication);
        response.addHeader("Authorization", "Bearer " + accessToken);

        // Refresh Token 발급
        Cookie cookie = createCookie(authentication);
        response.addCookie(cookie);

        // 로그인 성공 메세지
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"로그인이 되었습니다.\"}");
    }

    public Cookie createCookie(Authentication authentication) {
        String cookieName = "Refresh-Token";
        String cookieValue = jwtProvider.createRefreshToken(authentication);
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // 쿠키 속성 설정
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(15 * 60 * 60 * 24);
        return cookie;
    }
}
