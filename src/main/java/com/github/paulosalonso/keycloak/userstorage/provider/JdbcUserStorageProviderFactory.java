package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.configurations.ConfigurationsMapper;
import com.github.paulosalonso.keycloak.userstorage.configurations.ConfigurationsValidator;
import com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType;
import com.github.paulosalonso.keycloak.userstorage.database.*;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static org.keycloak.provider.ProviderConfigProperty.*;

public class JdbcUserStorageProviderFactory implements UserStorageProviderFactory<JdbcUserStorageProvider> {

    private static final String STORAGE_ID = "jdbc-user-storage";
    private final List<ProviderConfigProperty> CONFIG_PROPERTIES;

    public JdbcUserStorageProviderFactory() {
        CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
                .property(JDBC_URL, "JDBC URL Connection", "URL to connect database with JDBC", STRING_TYPE, null, null)
                .property(DB_USER, "Database user", "Database user", STRING_TYPE, null, null)
                .property(DB_PASSWORD, "Database password", "Database password", PASSWORD, null, null, true)

                .property(USER_QUERY,
                        "User query", "Query to get users in database. It must contain the fields referring to the id, username, email and password. It should not contain the WHERE clause.",
                        STRING_TYPE, null, null)
                .property(USER_ID_FIELD, "User id field", "Name of id field in user query", STRING_TYPE, null, null)
                .property(USER_USERNAME_FIELD, "Username field", "Name of username field in user query", STRING_TYPE, null, null)
                .property(USER_EMAIL_FIELD, "Email field", "Name of email field in user query", STRING_TYPE, null, null)
                .property(USER_PASSWORD_FIELD, "Password field", "Name of password field in user query", STRING_TYPE, null, null)

                .property(PASSWORD_ENCODE_TYPE,
                        "Password encode type", "If password is encoded in database, select the encode type",
                        LIST_TYPE, PasswordEncodeType.NONE.name(), PasswordEncodeType.asStringList())

                .property(ROLE_QUERY,
                        "Role query", "Query to get user roles. It must contain the fields referring to id, name and description. It shoud not contain the where clause.",
                        STRING_TYPE, null, null)
                .property(ROLE_ID_FIELD, "Role id field", "Name of id field in role query", STRING_TYPE, null, null)
                .property(ROLE_NAME_FIELD, "Role name field", "Name of name field in role query", STRING_TYPE, null, null)
                .property(ROLE_DESCRIPTION_FIELD, "Role description field", "Name of description field in role query", STRING_TYPE, null, null)
                .property(ROLE_USER_ID_FIELD, "Role user id field", "Name of user id field in role query", STRING_TYPE, null, null)
                .build();
    }

    @Override
    public String getId() {
        return STORAGE_ID;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel componentModel) throws ComponentValidationException {
        ConfigurationsValidator.validate(componentModel);
    }

    @Override
    public JdbcUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        var properties = ConfigurationsMapper.toProperties(componentModel);
        var connectionFactory = new ConnectionFactory(properties);
        var statementExecutor = new StatementExecutor(connectionFactory);
        var userDAO = new UserDAO(statementExecutor, properties, new UserMapper(properties));
        var roleDAO = new RoleDAO(statementExecutor, properties, new RoleMapper(properties));

        return new JdbcUserStorageProvider(
                userDAO, roleDAO, keycloakSession, componentModel, properties, new PasswordEncoderFactory());
    }
}
