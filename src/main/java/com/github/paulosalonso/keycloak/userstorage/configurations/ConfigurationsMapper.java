package com.github.paulosalonso.keycloak.userstorage.configurations;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;

import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.DB_PASSWORD;

public final class ConfigurationsMapper {

    private ConfigurationsMapper(){}

    public static Properties toProperties(ComponentModel componentModel) {
        MultivaluedHashMap<String, String> configurations = componentModel.getConfig();

        var properties = new Properties();
        properties.put(JDBC_URL, configurations.getFirst(JDBC_URL));
        properties.put(DB_USER, configurations.getFirst(DB_USER));
        properties.put(DB_PASSWORD, configurations.getFirst(DB_PASSWORD));
        properties.put(USER_QUERY, configurations.getFirst(USER_QUERY));
        properties.put(USER_ID_FIELD, configurations.getFirst(USER_ID_FIELD));
        properties.put(USER_USERNAME_FIELD, configurations.getFirst(USER_USERNAME_FIELD));
        properties.put(USER_EMAIL_FIELD, configurations.getFirst(USER_EMAIL_FIELD));

        return properties;
    }
}
