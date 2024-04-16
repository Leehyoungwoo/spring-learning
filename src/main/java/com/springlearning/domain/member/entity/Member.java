package com.springlearning.domain.member.entity;

import com.springlearning.domain.member.entity.type.Role;
import com.springlearning.global.security.converter.PasswordConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    @Convert(converter = PasswordConverter.class)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private boolean isAccountNonLocked;

    @NotNull
    private boolean isCredentialsNonExpired;

    @NotNull
    private boolean enabled;

    @NotNull
    private LocalDateTime credentialSet;

    @NotNull
    private Integer isCredentialFailCount;

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.credentialSet = LocalDateTime.now();
    }

    public void increaseCredentialFail() {
        this.isCredentialFailCount++;
    }

    public void lockAccount() {
        this.isAccountNonLocked = false;
    }

    public void resetLoginFailCount() {
        this.isCredentialFailCount = 0;
    }
}



