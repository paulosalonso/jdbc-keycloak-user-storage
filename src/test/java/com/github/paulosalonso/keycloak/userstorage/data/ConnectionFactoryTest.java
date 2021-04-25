package com.github.paulosalonso.keycloak.userstorage.data;

import com.github.paulosalonso.keycloak.userstorage.data.database.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class ConnectionFactoryTest {

    @InjectMocks
    private ConnectionFactory connectionFactory;

    @Mock
    private Properties properties;

    @Mock
    private Connection connection;

    @Test
    public void whenGetConnectionThenCallDriverManager() {
        when(properties.getProperty(JDBC_URL)).thenReturn("JDBC_URL");
        when(properties.getProperty(DB_USER)).thenReturn("DB_USER");
        when(properties.getProperty(DB_PASSWORD)).thenReturn("DB_PASSWORD");

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            driverManagerMock.when(() ->
                    DriverManager.getConnection("JDBC_URL", "DB_USER", "DB_PASSWORD")).thenReturn(connection);

            var producedConnection = connectionFactory.getConnection();

            assertThat(producedConnection).isSameAs(connection);
            driverManagerMock.verify(() -> DriverManager.getConnection("JDBC_URL", "DB_USER", "DB_PASSWORD"));
        }

        verify(properties).getProperty(JDBC_URL);
        verify(properties).getProperty(DB_USER);
        verify(properties).getProperty(DB_PASSWORD);
    }

    @Test
    public void whenDriverManagerThrowsSQLExceptionThenThrowsRuntimeExceptionWithSQLExceptionAsRootCause() {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
            var sqlException = new SQLException();

            driverManagerMock.when(() ->
                    DriverManager.getConnection(any(), any(), any())).thenThrow(sqlException);

            assertThatThrownBy(() -> connectionFactory.getConnection())
                    .isExactlyInstanceOf(RuntimeException.class)
                    .hasMessage("Error acquiring database connection")
                    .getCause()
                    .isSameAs(sqlException);
        }
    }
}
