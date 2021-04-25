package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.configurations.ConfigurationsMapper;
import com.github.paulosalonso.keycloak.userstorage.configurations.ConfigurationsValidator;
import com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType;
import com.github.paulosalonso.keycloak.userstorage.database.UserDAO;
import com.github.paulosalonso.keycloak.userstorage.provider.JdbcUserStorageProvider.JdbcUserStorageProviderBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;
import static org.assertj.core.api.Assertions.*;
import static org.keycloak.provider.ProviderConfigProperty.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JdbcUserStorageProviderFactoryTest {

    @Mock
    private KeycloakSession keycloakSession;

    @Mock
    private RealmModel realm;

    @Mock
    private ComponentModel componentModel;

    private JdbcUserStorageProviderFactory factory = new JdbcUserStorageProviderFactory();

    @Test
    public void whenGetFactoryIdThenReturnIt() {
        assertThat(factory.getId()).isEqualTo("jdbc-user-storage");
    }

    @Test
    public void wheGetConfigPropertiesThenReturnConfigurationList() {
        var configurations = factory.getConfigProperties();

        assertThat(configurations).hasSize(9);

        assertConfiguration(getConfigurationByName(configurations, JDBC_URL),
                JDBC_URL, "JDBC URL Connection", "URL to connect database with JDBC", STRING_TYPE, "jdbc:mysql://mysql/algafood", null, false);
        assertConfiguration(getConfigurationByName(configurations, DB_USER),
                DB_USER, "Database user", "Database user", STRING_TYPE, "root", null, false);
        assertConfiguration(getConfigurationByName(configurations, DB_PASSWORD),
                DB_PASSWORD, "Database password", "Database password", PASSWORD, "mysql", null, true);
        assertConfiguration(getConfigurationByName(configurations, USER_QUERY),
                USER_QUERY, "User query", "Query to get users in database. It must contain the fields referring to the id, username, email and password. It should not contain the WHERE clause.",
                STRING_TYPE, "SELECT id, email, senha FROM user", null, false);
        assertConfiguration(getConfigurationByName(configurations, USER_ID_FIELD),
                USER_ID_FIELD, "User id field", "Name of id field in user query", STRING_TYPE, "id", null, false);
        assertConfiguration(getConfigurationByName(configurations, USER_USERNAME_FIELD),
                USER_USERNAME_FIELD, "Username field", "Name of username field in user query", STRING_TYPE, "email", null, false);
        assertConfiguration(getConfigurationByName(configurations, USER_EMAIL_FIELD),
                USER_EMAIL_FIELD, "Email field", "Name of email field in user query", STRING_TYPE, "email", null, false);
        assertConfiguration(getConfigurationByName(configurations, USER_PASSWORD_FIELD),
                USER_PASSWORD_FIELD, "Password field", "Name of password field in user query", STRING_TYPE, "senha", null, false);
        assertConfiguration(getConfigurationByName(configurations, PASSWORD_ENCODE_TYPE),
                PASSWORD_ENCODE_TYPE, "Password encode type", "If password is encoded in database, select the encode type",
                LIST_TYPE, PasswordEncodeType.NONE.name(), PasswordEncodeType.asStringList(), false);
    }

    @Test
    public void whenValidateConfigurationsCallConfigurationsValidator() {
        try (MockedStatic<ConfigurationsValidator> validatorMock = mockStatic(ConfigurationsValidator.class)) {
            assertThatCode(() -> factory.validateConfiguration(keycloakSession, realm, componentModel))
                    .doesNotThrowAnyException();

            validatorMock.verify(() -> ConfigurationsValidator.validate(componentModel));
            verifyNoInteractions(keycloakSession, realm);
        }
    }

    @Test
    public void whenConfigurationsValidationFailsThenThrowsTheSameException() {
        try (MockedStatic<ConfigurationsValidator> validatorMock = mockStatic(ConfigurationsValidator.class)) {
            var exception = new RuntimeException();

            validatorMock.when(() -> ConfigurationsValidator.validate(componentModel)).thenThrow(exception);

            assertThatThrownBy(() -> factory.validateConfiguration(keycloakSession, realm, componentModel))
                    .isSameAs(exception);

            validatorMock.verify(() -> ConfigurationsValidator.validate(componentModel));
            verifyNoInteractions(keycloakSession, realm);
        }
    }

    @Test
    public void whenCreateThenReturnProvider() {
        var provider = JdbcUserStorageProvider.builder().build();

        try (var providerMock = mockStatic(JdbcUserStorageProvider.class);
             var mapperMock = mockStatic(ConfigurationsMapper.class)) {

            var providerBuilder = mock(JdbcUserStorageProviderBuilder.class);
            var properties = mock(Properties.class);
            providerMock.when(JdbcUserStorageProvider::builder).thenReturn(providerBuilder);
            mapperMock.when(() -> ConfigurationsMapper.toProperties(componentModel)).thenReturn(properties);
            when(providerBuilder.build()).thenReturn(provider);

            when(providerBuilder.componentModel(componentModel)).thenReturn(providerBuilder);
            when(providerBuilder.passwordEncoderFactory(any(PasswordEncoderFactory.class))).thenReturn(providerBuilder);
            when(providerBuilder.properties(properties)).thenReturn(providerBuilder);
            when(providerBuilder.session(keycloakSession)).thenReturn(providerBuilder);
            when(providerBuilder.userDAO(any(UserDAO.class))).thenReturn(providerBuilder);
            when(providerBuilder.build()).thenReturn(provider);

            var providerReturned = factory.create(keycloakSession, componentModel);

            assertThat(providerReturned).isSameAs(provider);

            providerMock.verify(JdbcUserStorageProvider::builder);
            mapperMock.verify(() -> ConfigurationsMapper.toProperties(componentModel));
            verify(providerBuilder).componentModel(componentModel);
            verify(providerBuilder).passwordEncoderFactory(any(PasswordEncoderFactory.class));
            verify(providerBuilder).properties(properties);
            verify(providerBuilder).session(keycloakSession);
            verify(providerBuilder).userDAO(any(UserDAO.class));
            verify(providerBuilder).build();
        }
    }

    private void assertConfiguration(ProviderConfigProperty configuration, String name,
        String label, String helpText, String type, Object defaultValue, List<String> options, boolean isSecret) {

        assertThat(configuration.getName()).isEqualTo(name);
        assertThat(configuration.getLabel()).isEqualTo(label);
        assertThat(configuration.getHelpText()).isEqualTo(helpText);
        assertThat(configuration.getType()).isEqualTo(type);
        assertThat(configuration.getDefaultValue()).isEqualTo(defaultValue);
        assertThat(configuration.getOptions()).isEqualTo(options);
        assertThat(configuration.isSecret()).isEqualTo(isSecret);
    }

    private ProviderConfigProperty getConfigurationByName(List<ProviderConfigProperty> configurations, String name) {
        return configurations.stream()
                .filter(configuration -> configuration.getName().equals(name))
                .findFirst()
                .get();
    }
}
