package com.springlearning.domain.member.application;

import com.springlearning.domain.member.dao.MemberRepository;
import com.springlearning.domain.member.dto.MemberJoinDto;
import com.springlearning.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void register(MemberJoinDto memberJoinDto) {
        Member newMember = memberJoinDto.toEntity();
        memberRepository.save(newMember);
    }
}
