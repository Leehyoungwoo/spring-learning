package com.springlearning.global.security.jwt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TokenDto(
        @NotNull
        String accessToken
) {
}
