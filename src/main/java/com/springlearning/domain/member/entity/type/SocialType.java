package com.springlearning.domain.member.entity.type;

public enum SocialType {
    NAVER, KAKAO, GOOGLE;

    public static SocialType fromString(String value) {
        for (SocialType socialType : SocialType.values()) {
            if (socialType.name().equalsIgnoreCase(value)) {
                return socialType;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
