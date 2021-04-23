package com.github.paulosalonso.keycloak.userstorage.configurations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PasswordEncodeTypeTest {

    @Test
    public void whenGetPasswordEncodeTypeOfNameThenReturnPasswordEncodeType() {
        var name = PasswordEncodeType.BCRYPT.name();
        var type = PasswordEncodeType.of(name);
        assertThat(type).isEqualByComparingTo(PasswordEncodeType.BCRYPT);
    }

    @Test
    public void whenGetPasswordEncodeTypeOfInvalidNameThenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> PasswordEncodeType.of("invalid-type"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password encode type: invalid-type")
                .hasNoCause();
    }
}
