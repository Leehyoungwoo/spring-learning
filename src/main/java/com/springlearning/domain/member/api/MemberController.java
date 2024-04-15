package com.springlearning.domain.member.api;

import com.springlearning.domain.member.application.MemberService;
import com.springlearning.domain.member.dto.MemberJoinDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public void register(@Valid @RequestBody MemberJoinDto memberJoinDto) {
        memberService.register(memberJoinDto);
    }
}
