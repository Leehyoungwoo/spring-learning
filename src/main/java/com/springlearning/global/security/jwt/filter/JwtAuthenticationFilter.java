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
        if (requestURI.equals("/api/login")
                && StringUtils.hasText(token)
                && jwtProvider.validateToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = jwtProvider.getOAuth2Authentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
