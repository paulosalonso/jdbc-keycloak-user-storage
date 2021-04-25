package com.github.paulosalonso.keycloak.userstorage.configurations;

import com.github.paulosalonso.keycloak.userstorage.data.database.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.paulosalonso.keycloak.userstorage.configurations.ConfigurationsMapper.toProperties;

@Slf4j
public final class ConfigurationsValidator {

    private ConfigurationsValidator(){}

    public static void validate(ComponentModel componentModel) {
        Connection connection = null;

        try {
            var properties = toProperties(componentModel);
            connection = new ConnectionFactory(properties).getConnection();
        } catch (Exception e) {
            throw new ComponentValidationException("Database properties are invalid: " + getRootCause(e), e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.warn("An error occurred when validating database connection", e);
            }
        }
    }

    private static String getRootCause(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }

        return e.getMessage();
    }
}
