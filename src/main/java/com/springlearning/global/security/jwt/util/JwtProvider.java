package com.springlearning.global.security.jwt.util;

import com.springlearning.global.security.dto.UserFormLoginDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final String AUTHORITIES_KEY = "authorities";
    private final String jwtHeaderKey;
    private final String jwtSecretKey;
    private final long refreshValidityInMilliseconds;
    private final long accessValidityInMilliseconds;
    private SecretKey key;

    public JwtProvider(
            @Value("${jwt.header}") String jwtHeaderKey,
            @Value("${jwt.secret}") String jwtSecretKey,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshValidityInMilliseconds,
            @Value("${jwt.access-token-validity-in-seconds}") Long accessValidityInMilliseconds
    ) {
        this.jwtHeaderKey = jwtHeaderKey;
        this.jwtSecretKey = jwtSecretKey;
        this.refreshValidityInMilliseconds = refreshValidityInMilliseconds * 1000;
        this.accessValidityInMilliseconds = accessValidityInMilliseconds * 1000;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, this.accessValidityInMilliseconds);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, this.refreshValidityInMilliseconds);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);
        String username = claims.get("username").toString();
        UserFormLoginDto principal = UserFormLoginDto.builder()
                .username(username)
                .authorities(authorities)
                .isEnabled(true)
                .isCredentialsNonExpired(true)
                .isAccountNonLocked(true)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority))
                        .collect(Collectors.toCollection(ArrayList::new));
        return authorities;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 비어있습니다.");
        }

        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearToken = request.getHeader(jwtHeaderKey);
        if (bearToken != null && bearToken.startsWith("Bearer ")) {
            return bearToken.replace("Bearer", "").trim();
        }

        return bearToken;
    }

    private String createToken(Authentication authentication, long tokenValidityInMilliseconds) {
        String authorites = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        UserFormLoginDto principal = (UserFormLoginDto) authentication.getPrincipal();
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + tokenValidityInMilliseconds);

        return Jwts.builder()
                .claim(AUTHORITIES_KEY, authorites)
                .claim("username", principal.getUsername())
                .signWith(key)
                .expiration(expirationDate)
                .compact();
    }
}
