package com.springlearning.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springlearning.domain.member.application.MemberService;
import com.springlearning.domain.member.dto.MemberJoinDto;
import com.springlearning.global.redis.service.RefreshTokenService;
import com.springlearning.global.security.config.SecurityConfig;
import com.springlearning.global.security.jwt.util.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest({MemberController.class, SecurityConfig.class})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입 테스트")
    void register() throws Exception {
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .username("test@test.com")
                .password("test-password")
                .build();

        String memberJoinDtoJson = objectMapper.writeValueAsString(memberJoinDto);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJoinDtoJson))
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("회원가입"))
                .andExpect(status().isOk());
    }
}