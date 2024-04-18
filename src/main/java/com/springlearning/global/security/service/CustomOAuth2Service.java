package com.springlearning.global.security.service;

import com.springlearning.domain.member.dao.SocialMemberRepository;
import com.springlearning.domain.member.entity.SocialMember;
import com.springlearning.domain.member.entity.type.Role;
import com.springlearning.domain.member.entity.type.SocialType;
import com.springlearning.global.security.dto.OAuth2LoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final SocialMemberRepository socialMemberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        log.info("user : " + oauth2User.getAttributes().toString());
        String clientId = userRequest.getClientRegistration().getRegistrationId();
        String email = switch (clientId) {
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
                log.info("response1 : " + response.toString());
                email = (String) response.get("email");
                yield email;
            }
            default -> throw new IllegalStateException("Unexpected value: " + clientId);
        };

        // DB에서 찾아보고 없으면 가입
        SocialMember socialMember = socialMemberRepository.findByEmailAndSocialType(email, SocialType.fromString(clientId))
                .orElseGet(() -> autoRegisterMember(oauth2User, clientId));

        return OAuth2LoginDto.builder()
                .username(socialMember.getEmail())
                .role(socialMember.getRole())
                .build();
    }

    @Transactional
    public SocialMember autoRegisterMember(OAuth2User oauth2User, String clientId) {
        Map<String, Object> response = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
        log.info("계정 생성 완료");
        return socialMemberRepository.save(SocialMember.builder()
                .socialType(SocialType.fromString(clientId))
                .email((String) response.get("email"))
                .role(Role.USER)
                .build());
    }
}
