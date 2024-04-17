package com.springlearning.global.security.service;

import com.springlearning.domain.member.dao.MemberRepository;
import com.springlearning.domain.member.entity.Member;
import com.springlearning.global.security.dto.UserFormLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 롤이랑 authorities가 두개 다 있을 필요 없고
    @Override
    public UserFormLoginDto loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username);

        return UserFormLoginDto.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .role(member.getRole())
                .isAccountNonLocked(member.isAccountNonLocked())
                .isCredentialsNonExpired(member.isCredentialsNonExpired())
                .isEnabled(member.isEnabled())
                .build();
    }
}
