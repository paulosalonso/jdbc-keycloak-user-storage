package com.github.paulosalonso.keycloak.userstorage.data.dao;

import com.github.paulosalonso.keycloak.userstorage.data.database.StatementExecutor;
import com.github.paulosalonso.keycloak.userstorage.data.mapper.RoleMapper;
import com.github.paulosalonso.keycloak.userstorage.data.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.ROLE_QUERY;
import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.ROLE_USER_ID_FIELD;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleDAOTest {

    private static final String BASE_QUERY = "SELECT id, name, description FROM role";
    private static final String COMPLETE_QUERY = BASE_QUERY + " WHERE %s = ?";

    private RoleDAO roleDAO;

    @Mock
    private StatementExecutor statementExecutor;

    @Mock
    private Properties properties;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Captor
    private ArgumentCaptor<Function<Connection, List<Role>>> functionCaptor;

    @Captor
    private ArgumentCaptor<String> queryCaptor;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        lenient().when(preparedStatement.getResultSet()).thenReturn(resultSet);
        lenient().when(properties.getProperty(ROLE_USER_ID_FIELD)).thenReturn("userId");
        lenient().when(properties.getProperty(ROLE_QUERY)).thenReturn(BASE_QUERY);

        roleDAO = new RoleDAO(statementExecutor, properties, roleMapper);

        verify(properties).getProperty(ROLE_USER_ID_FIELD);
    }

    @Test
    public void whenFindUserByIdThenReturnOptionalWithUser() throws SQLException {
        var roleList = List.of(Role.builder().build());

        when(statementExecutor.executeStatement(any(Function.class))).thenReturn(roleList);

        var result = roleDAO.getRolesByUserId("1");
        assertThat(result).isSameAs(roleList);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        when(roleMapper.map(resultSet)).thenReturn(roleList);

        var functionRoleList = function.apply(connection);
        assertThat(functionRoleList).isSameAs(roleList);

        verify(connection).prepareStatement(queryCaptor.capture());
        var query = queryCaptor.getValue();
        assertThat(query).isEqualTo(format(COMPLETE_QUERY, "userId"));

        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verify(preparedStatement).getResultSet();
        verify(roleMapper).map(resultSet);
    }

    @Test
    public void whenAnErrorOccursSearchingUserByIdThenThrowsRuntimeException() throws SQLException {
        var exception = new RuntimeException();

        when(statementExecutor.executeStatement(any(Function.class))).thenThrow(exception);

        assertThatThrownBy(() -> roleDAO.getRolesByUserId("1")).isSameAs(exception);
        verify(statementExecutor).executeStatement(functionCaptor.capture());
        var function = functionCaptor.getValue();

        doThrow(SQLException.class).when(preparedStatement).execute();

        assertThatThrownBy(() -> function.apply(connection))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(SQLException.class);

        verify(preparedStatement).setString(1, "1");
        verify(preparedStatement).execute();
        verifyNoMoreInteractions(preparedStatement);
        verifyNoInteractions(roleMapper);
    }
}
