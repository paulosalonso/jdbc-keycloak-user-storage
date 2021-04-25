package com.github.paulosalonso.keycloak.userstorage.data.mapper;

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
public class RoleMapperTest {

    @InjectMocks
    private RoleMapper mapper;

    @Mock
    private Properties properties;

    @Mock
    private ResultSet resultSet;

    @Test
    public void whenMapResultSetThenReturnRoleList() throws SQLException {
        when(properties.getProperty(ROLE_ID_FIELD)).thenReturn("id");
        when(properties.getProperty(ROLE_NAME_FIELD)).thenReturn("name");
        when(properties.getProperty(ROLE_DESCRIPTION_FIELD)).thenReturn("description");

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("id")).thenReturn("role-id");
        when(resultSet.getString("name")).thenReturn("role-name");
        when(resultSet.getString("description")).thenReturn("role-description");

        var roles = mapper.map(resultSet);

        assertThat(roles)
                .hasSize(1)
                .first()
                .satisfies(role -> {
                    assertThat(role.getId()).isEqualTo("role-id");
                    assertThat(role.getName()).isEqualTo("role-name");
                    assertThat(role.getDescription()).isEqualTo("role-description");
                });

        verify(properties).getProperty(ROLE_ID_FIELD);
        verify(properties).getProperty(ROLE_NAME_FIELD);
        verify(properties).getProperty(ROLE_DESCRIPTION_FIELD);

        verify(resultSet, times(2)).next();
        verify(resultSet).getString("id");
        verify(resultSet).getString("name");
        verify(resultSet).getString("description");
    }

    @Test
    public void givenAnEmptyResultSetThenReturnEmptyList() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        var roles = mapper.map(resultSet);

        assertThat(roles).isEmpty();

        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet);
        verifyNoInteractions(properties);
    }
}
