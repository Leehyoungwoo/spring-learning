package com.springlearning.domain.member.application;

import com.springlearning.domain.member.dto.MemberJoinDto;

public interface MemberService {

    Integer getLoginFailCount(String username);

    void register(MemberJoinDto memberJoinDto);

    void increaseLoginFailCount(String username);

    void resetLoginFailCount(String username);
}
