package com.springlearning.global.security.config;

import com.springlearning.domain.member.application.MemberService;
import com.springlearning.global.redis.service.RefreshTokenService;
import com.springlearning.global.security.handler.common.CustomAccessDeniedHandler;
import com.springlearning.global.security.handler.entrypoint.CustomAuthenticationEntryPoint;
import com.springlearning.global.security.handler.form.LoginAuthenticationFailureHandler;
import com.springlearning.global.security.handler.form.LoginAuthenticationSuccessHandler;
import com.springlearning.global.security.handler.social.SocialLoginAuthenticationFailureHandler;
import com.springlearning.global.security.handler.social.SocialLoginAuthenticationSuccessHandler;
import com.springlearning.global.security.jwt.filter.JwtAuthenticationFilter;
import com.springlearning.global.security.jwt.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final String[] URL_WHITE_LIST = {
            "/error", "/login", "/favicon.ico",
            "/actuator/**", "/actuator", "/api-docs/**", "/swagger-ui/**",
            "/swagger-resources/**", "/swagger-ui.html", "/api/token/**",
            "/oauth2/authorization/kakao", "/api/members, /api/login", "/auth/refresh"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(URL_WHITE_LIST)
                        .permitAll()
                        .requestMatchers("/**")
                        .hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .formLogin(
                        configure -> configure.loginProcessingUrl("/api/login")
                                .successHandler(new LoginAuthenticationSuccessHandler(memberService, refreshTokenService, jwtProvider))
                                .failureHandler(new LoginAuthenticationFailureHandler(memberService)))
                .oauth2Login(
                        configure -> configure.successHandler(new SocialLoginAuthenticationSuccessHandler(refreshTokenService, jwtProvider))
                                .failureHandler(new SocialLoginAuthenticationFailureHandler())
                )
                .exceptionHandling(
                        configurer -> configurer.accessDeniedHandler(new CustomAccessDeniedHandler())
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Cache-Control",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
