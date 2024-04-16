package com.springlearning.domain.member.dto;

import com.springlearning.domain.member.entity.Member;
import com.springlearning.domain.member.entity.type.Role;

import java.time.LocalDateTime;

public record MemberJoinDto(
        String username,
        String password
) {
    public Member toEntity() {
        return Member.builder()
                .username(this.username)
                .password(this.password)
                .role(Role.USER)
                .enabled(true)
                .isCredentialsNonExpired(true)
                .isAccountNonLocked(true)
                .credentialSet(LocalDateTime.now())
                .isCredentialFailCount(0)
                .build();
    }
}
