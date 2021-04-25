package com.github.paulosalonso.keycloak.userstorage.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigurationsMapperTest {

    @Mock
    private ComponentModel componentModel;

    @Mock
    private MultivaluedHashMap<String, String> configurations;

    @BeforeEach
    public void setUp() {
        when(componentModel.getConfig()).thenReturn(configurations);
        when(configurations.getFirst(JDBC_URL)).thenReturn("JDBC_URL");
        when(configurations.getFirst(DB_USER)).thenReturn("DB_USER");
        when(configurations.getFirst(DB_PASSWORD)).thenReturn("DB_PASSWORD");
        when(configurations.getFirst(USER_QUERY)).thenReturn("USER_QUERY");
        when(configurations.getFirst(USER_ID_FIELD)).thenReturn("USER_ID_FIELD");
        when(configurations.getFirst(USER_USERNAME_FIELD)).thenReturn("USER_USERNAME_FIELD");
        when(configurations.getFirst(USER_EMAIL_FIELD)).thenReturn("USER_EMAIL_FIELD");
        when(configurations.getFirst(USER_PASSWORD_FIELD)).thenReturn("USER_PASSWORD_FIELD");
        when(configurations.getFirst(PASSWORD_ENCODE_TYPE)).thenReturn("PASSWORD_ENCODE_TYPE");
        when(configurations.getFirst(ROLE_QUERY)).thenReturn("ROLE_QUERY");
        when(configurations.getFirst(ROLE_ID_FIELD)).thenReturn("ROLE_ID_FIELD");
        when(configurations.getFirst(ROLE_NAME_FIELD)).thenReturn("ROLE_NAME_FIELD");
        when(configurations.getFirst(ROLE_DESCRIPTION_FIELD)).thenReturn("ROLE_DESCRIPTION_FIELD");
        when(configurations.getFirst(ROLE_USER_ID_FIELD)).thenReturn("ROLE_USER_ID_FIELD");
    }

    @Test
    public void givenAComponentModelWhenMapThenReturnProperties() {
        var properties = ConfigurationsMapper.toProperties(componentModel);

        assertThat(properties.get(JDBC_URL)).isEqualTo("JDBC_URL");
        assertThat(properties.get(DB_USER)).isEqualTo("DB_USER");
        assertThat(properties.get(DB_PASSWORD)).isEqualTo("DB_PASSWORD");
        assertThat(properties.get(USER_QUERY)).isEqualTo("USER_QUERY");
        assertThat(properties.get(USER_ID_FIELD)).isEqualTo("USER_ID_FIELD");
        assertThat(properties.get(USER_USERNAME_FIELD)).isEqualTo("USER_USERNAME_FIELD");
        assertThat(properties.get(USER_EMAIL_FIELD)).isEqualTo("USER_EMAIL_FIELD");
        assertThat(properties.get(USER_PASSWORD_FIELD)).isEqualTo("USER_PASSWORD_FIELD");
        assertThat(properties.get(PASSWORD_ENCODE_TYPE)).isEqualTo("PASSWORD_ENCODE_TYPE");
        assertThat(properties.get(ROLE_QUERY)).isEqualTo("ROLE_QUERY");
        assertThat(properties.get(ROLE_ID_FIELD)).isEqualTo("ROLE_ID_FIELD");
        assertThat(properties.get(ROLE_NAME_FIELD)).isEqualTo("ROLE_NAME_FIELD");
        assertThat(properties.get(ROLE_DESCRIPTION_FIELD)).isEqualTo("ROLE_DESCRIPTION_FIELD");
        assertThat(properties.get(ROLE_USER_ID_FIELD)).isEqualTo("ROLE_USER_ID_FIELD");

        verify(componentModel).getConfig();
        verify(configurations).getFirst(JDBC_URL);
        verify(configurations).getFirst(DB_USER);
        verify(configurations).getFirst(DB_PASSWORD);
        verify(configurations).getFirst(USER_QUERY);
        verify(configurations).getFirst(USER_ID_FIELD);
        verify(configurations).getFirst(USER_USERNAME_FIELD);
        verify(configurations).getFirst(USER_EMAIL_FIELD);
        verify(configurations).getFirst(USER_PASSWORD_FIELD);
        verify(configurations).getFirst(PASSWORD_ENCODE_TYPE);
        verify(configurations).getFirst(ROLE_QUERY);
        verify(configurations).getFirst(ROLE_ID_FIELD);
        verify(configurations).getFirst(ROLE_NAME_FIELD);
        verify(configurations).getFirst(ROLE_DESCRIPTION_FIELD);
        verify(configurations).getFirst(ROLE_USER_ID_FIELD);
    }
}
