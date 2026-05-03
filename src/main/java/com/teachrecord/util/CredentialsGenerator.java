package com.teachrecord.util;

import java.security.SecureRandom;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CredentialsGenerator {

    private static final String PASS_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private final SecureRandom random = new SecureRandom();

    public String newLoginUsername() {
        return "s_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public String newPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASS_CHARS.charAt(random.nextInt(PASS_CHARS.length())));
        }
        return sb.toString();
    }
}
