package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class PasswordEncoderFactory {

    private final Map<PasswordEncodeType, PasswordEncoder> cache = new HashMap<>();

    public PasswordEncoder getPasswordEncoder(PasswordEncodeType type) {
        if (cache.containsKey(type)) {
            return cache.get(type);
        }

        var passwordEncoder = createPasswordEncoder(type);
        cache.put(type, passwordEncoder);
        return passwordEncoder;
    }

    private PasswordEncoder createPasswordEncoder(PasswordEncodeType type) {
        switch (type) {
            case BCRYPT: return new BCryptPasswordEncoder();
            case MD5: return new MessageDigestPasswordEncoder("MD5");
            default: return NoOpPasswordEncoder.getInstance();
        }
    }
}
