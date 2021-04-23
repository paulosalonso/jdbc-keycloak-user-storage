package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

import static com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType.BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PasswordEncoderFactoryTest {

    private PasswordEncoderFactory factory = new PasswordEncoderFactory();

    @Test
    public void whenGetPasswordEncodeThenReturnBCryptPasswordEncode() {
        var passwordEncoder = factory.getPasswordEncoder(BCRYPT);
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void whenGetPasswordEncoderThenReturnMessageDigestPasswordEncoderWithMD5Algorithm() throws NoSuchFieldException, IllegalAccessException {
        var passwordEncoder = factory.getPasswordEncoder(PasswordEncodeType.MD5);
        assertThat(passwordEncoder).isInstanceOf(MessageDigestPasswordEncoder.class);

        var field = passwordEncoder.getClass().getDeclaredField("digester");
        field.setAccessible(true);
        var digester = field.get(passwordEncoder);
        field = digester.getClass().getDeclaredField("algorithm");
        field.setAccessible(true);
        var algorithm = field.get(digester);

        assertThat(algorithm).isEqualTo("MD5");
    }

    @Test
    public void whenGetPasswordEncodeThenReturnNoOpEncode() {
        var passwordEncoder = factory.getPasswordEncoder(PasswordEncodeType.NONE);
        assertThat(passwordEncoder).isInstanceOf(NoOpPasswordEncoder.class);
    }

    @Test
    public void whenGetPasswordEncoderModeThanOneTimeThenReturnCachedValue() throws NoSuchFieldException, IllegalAccessException {
        var cacheSpy = spy(new HashMap<PasswordEncodeType, PasswordEncoder>());
        var cacheField = factory.getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(factory, cacheSpy);

        var passwordEncoder = factory.getPasswordEncoder(BCRYPT);
        factory.getPasswordEncoder(BCRYPT);

        verify(cacheSpy, times(2)).containsKey(BCRYPT);
        verify(cacheSpy).put(BCRYPT, passwordEncoder);
        verify(cacheSpy).get(BCRYPT);
    }
}