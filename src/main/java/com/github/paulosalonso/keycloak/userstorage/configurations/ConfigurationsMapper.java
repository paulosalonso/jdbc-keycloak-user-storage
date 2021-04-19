package com.github.paulosalonso.keycloak.userstorage.configurations;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;

import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.DB_PASSWORD;

@Slf4j
public final class ConfigurationsMapper {

    private ConfigurationsMapper(){}

    public static Properties toProperties(ComponentModel componentModel) {
        MultivaluedHashMap<String, String> configurations = componentModel.getConfig();

        var properties = new Properties();

        log.debug("################### Getting JDBC User Storage configurations ###################");

        properties.put(JDBC_URL, configurations.getFirst(JDBC_URL));
        log.debug("JDBC_URL: {}", properties.get(JDBC_URL));

        properties.put(DB_USER, configurations.getFirst(DB_USER));
        log.debug("DB_USER: {}", properties.get(DB_USER));

        properties.put(DB_PASSWORD, configurations.getFirst(DB_PASSWORD));
        log.debug("DB_PASSWORD: {}", properties.get(DB_PASSWORD));

        properties.put(USER_QUERY, configurations.getFirst(USER_QUERY));
        log.debug("USER_QUERY: {}", properties.get(USER_QUERY));

        properties.put(USER_ID_FIELD, configurations.getFirst(USER_ID_FIELD));
        log.debug("USER_ID_FIELD: {}", properties.get(USER_ID_FIELD));

        properties.put(USER_USERNAME_FIELD, configurations.getFirst(USER_USERNAME_FIELD));
        log.debug("USER_USERNAME_FIELD: {}", properties.get(USER_USERNAME_FIELD));

        properties.put(USER_EMAIL_FIELD, configurations.getFirst(USER_EMAIL_FIELD));
        log.debug("USER_EMAIL_FIELD: {}", properties.get(USER_EMAIL_FIELD));

        properties.put(USER_PASSWORD_FIELD, configurations.getFirst(USER_PASSWORD_FIELD));
        log.debug("USER_PASSWORD_FIELD: {}", properties.get(USER_PASSWORD_FIELD));

        properties.put(PASSWORD_ENCODE_TYPE, configurations.getFirst(PASSWORD_ENCODE_TYPE));
        log.debug("PASSWORD_ENCODE_TYPE: {}", properties.get(PASSWORD_ENCODE_TYPE));

        return properties;
    }
}
