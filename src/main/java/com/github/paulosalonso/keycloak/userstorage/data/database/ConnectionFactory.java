package com.github.paulosalonso.keycloak.userstorage.data.database;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;

@RequiredArgsConstructor
public class ConnectionFactory {

    private final Properties properties;

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(properties.getProperty(JDBC_URL),
                    properties.getProperty(DB_USER), properties.getProperty(DB_PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException("Error acquiring database connection", e);
        }
    }
}
