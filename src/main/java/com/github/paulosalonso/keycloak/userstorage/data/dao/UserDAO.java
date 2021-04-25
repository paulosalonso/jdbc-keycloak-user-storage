package com.github.paulosalonso.keycloak.userstorage.data.dao;

import com.github.paulosalonso.keycloak.userstorage.data.database.StatementExecutor;
import com.github.paulosalonso.keycloak.userstorage.data.mapper.UserMapper;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;

@Slf4j
@RequiredArgsConstructor
public class UserDAO {

    private final StatementExecutor executor;
    private final Properties properties;
    private final UserMapper mapper;

    public Optional<User> findById(String id) {
        return executor.executeStatement(connection -> {
            try {
                var statement = connection.prepareStatement(prepareQuery(USER_ID_FIELD));
                statement.setString(1, id);
                statement.execute();

                var resultSet = statement.getResultSet();

                return mapper.map(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Optional<User> findByUsername(String name) {
        return executor.executeStatement(connection -> {
            try {
                var statement = connection.prepareStatement(prepareQuery(USER_USERNAME_FIELD));
                statement.setString(1, name);
                statement.execute();

                var resultSet = statement.getResultSet();

                return mapper.map(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException("Error when finding user", e);
            }
        });
    }

    public Optional<User> findByEmail(String email) {
        return executor.executeStatement(connection -> {
            try {
                var statement = connection.prepareStatement(prepareQuery(USER_EMAIL_FIELD));
                statement.setString(1, email);
                statement.execute();

                var resultSet = statement.getResultSet();

                return mapper.map(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException("Error when finding user", e);
            }
        });
    }

    public Optional<String> findPasswordByUserId(String id) {
        return executor.executeStatement(connection -> {
            try {
                var statement = connection.prepareStatement(prepareQuery(USER_ID_FIELD));
                statement.setString(1, id);
                statement.execute();

                var resultSet = statement.getResultSet();

                if (resultSet.next()) {
                    return Optional.of(resultSet.getString(properties.getProperty(USER_PASSWORD_FIELD)));
                }

                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException("Error when finding user password", e);
            }
        });
    }

    private String prepareQuery(String wantedField) {
        var query = String.format("%s WHERE %s = ?", properties.getProperty(USER_QUERY), properties.getProperty(wantedField));
        log.debug("Searching user with query: {}", query);
        return query;
    }

}
