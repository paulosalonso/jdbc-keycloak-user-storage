package com.github.paulosalonso.keycloak.userstorage.database;

import com.github.paulosalonso.keycloak.userstorage.model.User;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;

@RequiredArgsConstructor
public class ResultSetMapper {

    private final Properties properties;

    public Optional<User> map(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(User.builder()
                    .id(resultSet.getString(properties.getProperty(USER_ID_FIELD)))
                    .username(resultSet.getString(properties.getProperty(USER_USERNAME_FIELD)))
                    .email(resultSet.getString(properties.getProperty(USER_EMAIL_FIELD)))
                    .build());
        }

        return Optional.empty();
    }

}
