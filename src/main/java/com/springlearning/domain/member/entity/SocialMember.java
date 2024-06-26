package com.springlearning.domain.member.entity;

import com.springlearning.domain.member.entity.type.Role;
import com.springlearning.domain.member.entity.type.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMember {

    @Id
    @Column(name = "social_member_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @NotNull
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
}
