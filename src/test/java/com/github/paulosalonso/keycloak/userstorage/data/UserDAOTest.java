package com.github.paulosalonso.keycloak.userstorage.data;

import com.github.paulosalonso.keycloak.userstorage.data.dao.UserDAO;
import com.github.paulosalonso.keycloak.userstorage.data.database.ConnectionFactory;
import com.github.paulosalonso.keycloak.userstorage.data.mapper.UserMapper;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

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
    private Properties properties;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(connectionFactory.getConnection()).thenReturn(connection);
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        lenient().when(preparedStatement.getResultSet()).thenReturn(resultSet);
        lenient().when(properties.getProperty(USER_QUERY)).thenReturn(BASE_QUERY);
    }

    @Test
    public void whenFindUserByIdThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var result = userDAO.findById("1");

        assertThat(result).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, ID_FIELD));

        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
        verify(connection).close();
    }

    @Test
    public void whenAnErrorOccursSearchingUserByIdThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> userDAO.findById("1"))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
        verify(connection).close();
    }

    @Test
    public void whenFindUserByUsernameThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(properties.getProperty(USER_USERNAME_FIELD)).thenReturn(USERNAME_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var result = userDAO.findByUsername("fulano");

        assertThat(result).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_USERNAME_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, USERNAME_FIELD));

        verify(preparedStatement).setString(1, "fulano");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
        verify(connection).close();
    }

    @Test
    public void whenAnErrorOccursSearchingUserByUsernameThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> userDAO.findByUsername("fulano"))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_USERNAME_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "fulano");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
        verify(connection).close();
    }

    @Test
    public void whenFindUserByEmailThenReturnOptionalWithUser() throws SQLException {
        var userOptional = Optional.of(User.builder().build());

        when(properties.getProperty(USER_EMAIL_FIELD)).thenReturn(EMAIL_FIELD);
        when(userMapper.map(resultSet)).thenReturn(userOptional);

        var result = userDAO.findByEmail("fulano@mail.com");

        assertThat(result).isSameAs(userOptional);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_EMAIL_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();

        var queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());

        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, EMAIL_FIELD));

        verify(preparedStatement).setString(1, "fulano@mail.com");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(userMapper).map(resultSet);
        verify(connection).close();
    }

    @Test
    public void whenAnErrorOccursSearchingUserByEmailThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> userDAO.findByEmail("fulano@mail.com"))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_EMAIL_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "fulano@mail.com");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(userMapper);
        verify(connection).close();
    }

    @Test
    public void whenFindPasswordByUserIdThenReturnOptionalWithPassword() throws SQLException {
        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(properties.getProperty(USER_PASSWORD_FIELD)).thenReturn(PASSWORD_FIELD);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(PASSWORD_FIELD)).thenReturn("P@ssw0rd");

        var result = userDAO.findPasswordByUserId("1");

        assertThat(result)
                .isPresent()
                .hasValue("P@ssw0rd");

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verify(properties).getProperty(USER_PASSWORD_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();

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
        verify(connection).close();
    }

    @Test
    public void whenFindPasswordByNonexistentUserIdThenReturnEmptyOptional() throws SQLException {
        when(properties.getProperty(USER_ID_FIELD)).thenReturn(ID_FIELD);
        when(resultSet.next()).thenReturn(false);

        var result = userDAO.findPasswordByUserId("1");

        assertThat(result).isEmpty();

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet);
        verifyNoInteractions(userMapper);
        verify(connection).close();
    }

    @Test
    public void whenAnErrorOccursSearchingPasswordByUserIdThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> userDAO.findPasswordByUserId("1"))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(properties).getProperty(USER_QUERY);
        verify(properties).getProperty(USER_ID_FIELD);
        verifyNoMoreInteractions(properties);
        verify(connectionFactory).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(resultSet);
        verifyNoInteractions(userMapper);
        verify(connection).close();
    }

    @Test
    public void whenAnErrorOccursGettingConnectionThenThrowsRuntimeException() {
        when(connectionFactory.getConnection()).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> userDAO.findById("1")).isExactlyInstanceOf(RuntimeException.class);

        verify(connectionFactory).getConnection();
        verifyNoInteractions(connection, properties, preparedStatement, userMapper);
    }

    @Test
    public void whenAnErrorOccursClosingConnectionThenThrowsRuntimeException() throws SQLException {
        doThrow(SQLException.class).when(connection).close();

        assertThatThrownBy(() -> userDAO.findById("1"))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class)
                .hasMessage("Error closing connection");

        verify(connection).close();
    }
}
