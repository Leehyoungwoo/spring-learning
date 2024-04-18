package com.springlearning.global.security.dto;

import com.springlearning.domain.member.entity.type.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@Builder
public class OAuth2LoginDto implements OAuth2User {

    private String username;

    private Role role;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", this.username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return this.username;
    }
}
