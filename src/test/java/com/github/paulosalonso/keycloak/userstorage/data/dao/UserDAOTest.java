package com.github.paulosalonso.keycloak.userstorage.data.dao;

import com.github.paulosalonso.keycloak.userstorage.data.database.StatementExecutor;
import com.github.paulosalonso.keycloak.userstorage.data.mapper.UserMapper;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDAOTest {

    private static final String ID_FIELD = "id";
    private static final String USERNAME_FIELD = "name";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String BASE_QUERY = "SELECT id, name, email, password FROM user";
    private static final String COMPLETE_QUERY = BASE_QUERY + " WHERE %s = ?";

    @InjectMocks
    private UserDAO userDAO;

    @Mock
    private StatementExecutor statementExecutor;

    @Mock
    private Properties properties;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Captor
    private ArgumentCaptor<Function<Connection, Optional<User>>> functionCaptor;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        lenient().when(preparedStatement.getResultSet()).thenReturn(resultSet);
        lenient().when(properties.getProperty(USER_QUERY)).thenReturn(BASE_QUERY);
    }

    @Test
    public void whenFindUserByIdThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(userOptional);

        var result = userDAO.findById("1");
        assertThat(result).isSameAs(userOptional);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var functionUserOptional = function.apply(connection);
        assertThat(functionUserOptional).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, ID_FIELD));

        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
    }

    @Test
    public void whenAnErrorOccursSearchingUserByIdThenThrowsRuntimeException() throws SQLException {
        var exception = new RuntimeException();

        when(statementExecutor.executeStatement(any(Function.class))).thenThrow(exception);

        assertThatThrownBy(() -> userDAO.findById("1")).isSameAs(exception);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> function.apply(connection))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void whenFindUserByUsernameThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(userOptional);

        var result = userDAO.findByUsername("fulano");
        assertThat(result).isSameAs(userOptional);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(properties.getProperty(USER_USERNAME_FIELD)).thenReturn(USERNAME_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var functionUserOptional = function.apply(connection);
        assertThat(functionUserOptional).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_USERNAME_FIELD);
        verifyNoMoreInteractions(properties);

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, USERNAME_FIELD));

        verify(preparedStatement).setString(1, "fulano");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
    }

    @Test
    public void whenAnErrorOccursSearchingUserByUsernameThenThrowsRuntimeException() throws SQLException {
        var exception = new RuntimeException();

        when(statementExecutor.executeStatement(any(Function.class))).thenThrow(exception);

        assertThatThrownBy(() -> userDAO.findByUsername("fulano")).isSameAs(exception);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> function.apply(connection))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_USERNAME_FIELD);
        verifyNoMoreInteractions(properties);
        verify(preparedStatement).setString(1, "fulano");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void whenFindUserByEmailThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(userOptional);

        var result = userDAO.findByEmail("fulano@mail.com");
        assertThat(result).isSameAs(userOptional);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(properties.getProperty(USER_EMAIL_FIELD)).thenReturn(EMAIL_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var functionUserOptional = function.apply(connection);
        assertThat(functionUserOptional).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_EMAIL_FIELD);
        verifyNoMoreInteractions(properties);

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, EMAIL_FIELD));

        verify(preparedStatement).setString(1, "fulano@mail.com");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
    }

    @Test
    public void whenAnErrorOccursSearchingUserByEmailThenThrowsRuntimeException() throws SQLException {
        var exception = new RuntimeException();

        when(statementExecutor.executeStatement(any(Function.class))).thenThrow(exception);

        assertThatThrownBy(() -> userDAO.findByEmail("fulano@mail.com")).isSameAs(exception);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> function.apply(connection))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_EMAIL_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "fulano@mail.com");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void whenFindPasswordByUserIdThenReturnOptionalWithPassword() throws SQLException {
        var passwordOptional = Optional.of("P@ssw0rd");

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(passwordOptional);

        var result = userDAO.findPasswordByUserId("1");
        assertThat(result).isSameAs(passwordOptional);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(properties.getProperty(USER_PASSWORD_FIELD)).thenReturn(PASSWORD_FIELD);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(PASSWORD_FIELD)).thenReturn("P@ssw0rd");

        var functionPasswordOptional = function.apply(connection);
        assertThat(functionPasswordOptional).isEqualTo(passwordOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verify(properties).getProperty(USER_PASSWORD_FIELD);
        verifyNoMoreInteractions(properties);

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, ID_FIELD));

        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(resultSet).next();
        verify(resultSet).getString(PASSWORD_FIELD);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void whenFindPasswordByNonexistentUserIdThenReturnEmptyOptional() throws SQLException {
        var passwordOptional = Optional.empty();

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(passwordOptional);

        var result = userDAO.findPasswordByUserId("1");
        assertThat(result).isSameAs(passwordOptional);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(resultSet.next()).thenReturn(false);

        var functionPasswordOptional = function.apply(connection);
        assertThat(functionPasswordOptional).isSameAs(passwordOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void whenAnErrorOccursSearchingPasswordByUserIdThenThrowsRuntimeException() throws SQLException {
        var exception = new RuntimeException();

        when(statementExecutor.executeStatement(any(Function.class))).thenThrow(exception);

        assertThatThrownBy(() -> userDAO.findPasswordByUserId("1")).isSameAs(exception);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> function.apply(connection))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(resultSet);
        verifyNoInteractions(userMapper);
    }
}
