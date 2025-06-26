package com.fintech.usermanagement.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AppUtils {

    private static final String NUMERIC_CHARACTERS = "0123456789";

    public static String generateRandomNumeric(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(NUMERIC_CHARACTERS.length());
            result.append(NUMERIC_CHARACTERS.charAt(randomIndex));
        }

        return result.toString();
    }
}
