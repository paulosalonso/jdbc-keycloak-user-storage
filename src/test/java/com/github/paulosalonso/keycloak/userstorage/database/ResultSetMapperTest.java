package com.github.paulosalonso.keycloak.userstorage.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultSetMapperTest {

    @InjectMocks
    private ResultSetMapper resultSetMapper;

    @Mock
    private Properties properties;

    @Mock
    private ResultSet resultSet;

    @Test
    public void whenMapResultSetThenReturnOptionWithUser() throws SQLException {
        when(properties.getProperty(USER_ID_FIELD)).thenReturn("id");
        when(properties.getProperty(USER_USERNAME_FIELD)).thenReturn("username");
        when(properties.getProperty(USER_EMAIL_FIELD)).thenReturn("email");

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn("1");
        when(resultSet.getString("username")).thenReturn("fulano");
        when(resultSet.getString("email")).thenReturn("fulano@mail.com");

        var userOptional = resultSetMapper.map(resultSet);

        assertThat(userOptional)
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getId()).isEqualTo("1");
                    assertThat(user.getUsername()).isEqualTo("fulano");
                    assertThat(user.getEmail()).isEqualTo("fulano@mail.com");
                });

        verify(properties).getProperty(USER_ID_FIELD);
        verify(properties).getProperty(USER_USERNAME_FIELD);
        verify(properties).getProperty(USER_EMAIL_FIELD);

        verify(resultSet).next();
        verify(resultSet).getString("id");
        verify(resultSet).getString("username");
        verify(resultSet).getString("email");
    }

    @Test
    public void givenAEmptyResultSetWhenMapThenReturnEmptyOptional() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        var userOptional = resultSetMapper.map(resultSet);

        assertThat(userOptional).isEmpty();

        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet);
        verifyNoInteractions(properties);
    }
}