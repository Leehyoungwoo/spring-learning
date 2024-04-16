package com.springlearning.global.security.service;

import com.springlearning.domain.member.dao.MemberRepository;
import com.springlearning.domain.member.entity.Member;
import com.springlearning.global.security.UserFormLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserFormLoginDto loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username);

        return UserFormLoginDto.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .role(member.getRole())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())))
                .isAccountNonLocked(member.isAccountNonLocked())
                .isCredentialsNonExpired(member.isCredentialsNonExpired())
                .isEnabled(member.isEnabled())
                .build();
    }
}