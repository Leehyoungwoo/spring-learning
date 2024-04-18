package com.springlearning.domain.member.dao;

import com.springlearning.domain.member.entity.SocialMember;
import com.springlearning.domain.member.entity.type.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {

    Optional<SocialMember> findByEmailAndSocialType(String email, SocialType socialType);
}
