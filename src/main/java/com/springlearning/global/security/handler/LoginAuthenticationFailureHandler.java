package com.springlearning.global.security.handler;

import com.google.gson.Gson;
import com.springlearning.domain.member.application.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final MemberService memberService;

    /*
     * HttpServletRequest : request 정보
     * HttpServletResponse : Response에 대해 설정할 수 있는 변수
     * AuthenticationException : 로그인 실패 시 예외에 대한 정보
     */

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        StringBuilder errorMessage = new StringBuilder();

        Map<String, Integer> failCountMap = new HashMap<>();
        failCountMap.put("fail", 0);
        failCountMap.put("limit", 5);

        Map<String, Object> responseMap = new HashMap<>();

        if (exception instanceof BadCredentialsException) {
            memberService.increaseLoginFailCount(username);
            Integer loginFailCount = memberService.getLoginFailCount(username);
            failCountMap.put("fail", loginFailCount);
            errorMessage.append("비밀번호가 일치하지 않습니다.");
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage.append("내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.");
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage.append("존재하지 않는 계정입니다. 회원가입 후 로그인해주세요.");
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage.append("인증 요청이 거부되었습니다. 관리자에게 문의하세요.");
        } else {
            errorMessage.append("알 수 없는 오류로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.");
        }
        responseMap.put("failCount", failCountMap);
        responseMap.put("errorMessage", errorMessage.toString());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(responseMap);
        response.getWriter().write(jsonResponse);
    }
}