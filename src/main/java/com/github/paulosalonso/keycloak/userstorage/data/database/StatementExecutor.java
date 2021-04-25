package com.github.paulosalonso.keycloak.userstorage.data.database;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

@RequiredArgsConstructor
public class StatementExecutor {

    private final ConnectionFactory connectionFactory;

    public <T> T executeStatement(Function<Connection, T> statement) {
        Connection connection = null;

        try {
            connection = connectionFactory.getConnection();
            return statement.apply(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Error closing connection", e);
                }
            }
        }
    }

}
