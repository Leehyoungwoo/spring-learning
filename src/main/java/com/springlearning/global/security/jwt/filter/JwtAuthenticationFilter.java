package com.springlearning.global.security.jwt.filter;

import com.springlearning.global.security.jwt.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtProvider.resolveToken(request);
        log.info("token : " + token);
        String requestURI = request.getRequestURI();
        log.info("url : " + requestURI);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            Authentication authentication = null;

            // OAuth2 로그인 처리
            if (requestURI.equals("/oauth2/authorization/kakao")) {
                authentication = jwtProvider.getOAuth2Authentication(token);
            }
            // 기타 일반 요청에 대한 인증 처리
            else {
                authentication = jwtProvider.getAuthentication(token);
            }

            // SecurityContext에 인증 정보 설정
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
