package com.springlearning.domain.member.application;

import com.springlearning.domain.member.dto.MemberJoinDto;

public interface MemberService {

    void register(MemberJoinDto memberJoinDto);
}
