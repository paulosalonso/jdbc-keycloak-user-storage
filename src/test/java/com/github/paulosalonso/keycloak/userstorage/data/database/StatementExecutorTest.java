package com.github.paulosalonso.keycloak.userstorage.data.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementExecutorTest {

    @InjectMocks
    private StatementExecutor executor;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Function<Connection, String> function;

    @BeforeEach
    public void setUp() {
        lenient().when(connectionFactory.getConnection()).thenReturn(connection);
    }

    @Test
    public void whenExecuteStatementThenReturnFunctionResult() throws SQLException {
        when(function.apply(connection)).thenReturn("result");
        assertThat(executor.executeStatement(function)).isEqualTo("result");
        verify(connectionFactory).getConnection();
        verify(function).apply(connection);
        verify(connection).close();
    }

    @Test
    public void whenOccursErrorGettingConnectionThenDoesNotCallCloseMethodAndThrowsSameException() {
        var exception = new RuntimeException();

        when(connectionFactory.getConnection()).thenThrow(exception);
        assertThatThrownBy(() -> executor.executeStatement(function)).isSameAs(exception);
        verify(connectionFactory).getConnection();
        verifyNoInteractions(function);
        verifyNoInteractions(connection);
    }

    @Test
    public void whenOccursErrorInvokingApplyMethodOnFunctionThenCloseConnectionAndThrowsSameException() throws SQLException {
        var exception = new RuntimeException();

        when(function.apply(connection)).thenThrow(exception);
        assertThatThrownBy(() -> executor.executeStatement(function)).isSameAs(exception);
        verify(connectionFactory).getConnection();
        verify(function).apply(connection);
        verify(connection).close();
    }

    @Test
    public void whenOccursErrorClosingConnectionThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(connection).close();
        assertThatThrownBy(() -> executor.executeStatement(function))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class)
                .hasMessage("Error closing connection");
        verify(connection).close();
    }
}
