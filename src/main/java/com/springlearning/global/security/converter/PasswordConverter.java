package com.springlearning.global.security.converter;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordConverter implements AttributeConverter<String, String> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String raw) {
        return passwordEncoder.encode(raw);
    }

    @Override
    public String convertToEntityAttribute(String encoded) {
        return passwordEncoder.encode(encoded);
    }
}
