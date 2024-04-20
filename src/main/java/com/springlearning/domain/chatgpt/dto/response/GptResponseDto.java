package com.springlearning.domain.chatgpt.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GptResponseDto(
        @NotBlank
        String answer
) {
}
