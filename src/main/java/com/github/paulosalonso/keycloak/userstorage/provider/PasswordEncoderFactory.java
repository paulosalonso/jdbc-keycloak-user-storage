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
        return cache.getOrDefault(type, createPasswordEncoder(type));
    }

    private PasswordEncoder createPasswordEncoder(PasswordEncodeType type) {
        PasswordEncoder encoder;

        switch (type) {
            case BCRYPT: encoder = new BCryptPasswordEncoder(); break;
            case MD5: encoder = new MessageDigestPasswordEncoder("MD5"); break;
            default: encoder = NoOpPasswordEncoder.getInstance();
        }

        cache.put(type, encoder);

        return encoder;
    }
}
