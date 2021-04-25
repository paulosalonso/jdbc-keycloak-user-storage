package com.github.paulosalonso.keycloak.userstorage.data.dao;

import com.github.paulosalonso.keycloak.userstorage.data.database.StatementExecutor;
import com.github.paulosalonso.keycloak.userstorage.data.mapper.RoleMapper;
import com.github.paulosalonso.keycloak.userstorage.model.Role;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.ROLE_QUERY;
import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.ROLE_USER_ID_FIELD;

@Slf4j
public class RoleDAO {

    private static final String QUERY_PLACEHOLDER = "%s WHERE %s = ?";

    private final StatementExecutor executor;
    private final RoleMapper mapper;
    private final String query;

    public RoleDAO(StatementExecutor executor, Properties properties, RoleMapper mapper) {
        this.executor = executor;
        this.mapper = mapper;
        query = String.format(QUERY_PLACEHOLDER,
                properties.getProperty(ROLE_QUERY), properties.getProperty(ROLE_USER_ID_FIELD));
    }

    public List<Role> getRolesByUserId(String userId) {
        return executor.executeStatement(connection -> {

            log.debug("Searching roles by user id {} with query: {}", userId, query);

            try {
                var statement = connection.prepareStatement(query);
                statement.setString(1, userId);
                statement.execute();

                var resultSet = statement.getResultSet();

                return mapper.map(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
