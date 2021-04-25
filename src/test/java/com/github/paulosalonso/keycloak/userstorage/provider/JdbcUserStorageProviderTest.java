package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.database.UserDAO;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.PASSWORD_ENCODE_TYPE;
import static com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType.BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JdbcUserStorageProviderTest {

    @InjectMocks
    private JdbcUserStorageProvider provider;

    @Mock
    private UserDAO userDAO;

    @Mock
    private KeycloakSession session;

    @Mock
    private ComponentModel componentModel;

    @Mock
    private Properties properties;

    @Mock
    private PasswordEncoderFactory passwordEncoderFactory;

    @Mock
    private RealmModel realmModel;

    @Mock
    private UserModel userModel;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CredentialInput credentialInput;

    @Test
    public void whenGetUserByIdThenReturnUserModel() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();
        var user = User.builder().build();

        when(userDAO.findById("external-id")).thenReturn(Optional.of(user));

        var userModel = provider.getUserById(keycloakId, realmModel);

        assertThat(userModel).isNotNull();

        var decoratedUser = getDecoratedUser(userModel);

        assertThat(decoratedUser).isSameAs(user);

        verify(userDAO).findById("external-id");
    }

    @Test
    public void whenGetUserByNonexistentIdThenReturnNull() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();

        when(userDAO.findById("external-id")).thenReturn(Optional.empty());

        var userModel = provider.getUserById(keycloakId, realmModel);

        assertThat(userModel).isNull();
        verify(userDAO).findById("external-id");
    }

    @Test
    public void whenGetUserByUsernameThenReturnUserModel() {
        var user = User.builder().build();

        when(userDAO.findByUsername("fulano")).thenReturn(Optional.of(user));

        var userModel = provider.getUserByUsername("fulano", realmModel);

        assertThat(userModel).isNotNull();

        var decoratedUser = getDecoratedUser(userModel);

        assertThat(decoratedUser).isSameAs(user);

        verify(userDAO).findByUsername("fulano");
    }

    @Test
    public void whenGetUserByNonexistentUsernameThenReturnNull() {
        when(userDAO.findByUsername("fulano")).thenReturn(Optional.empty());

        var userModel = provider.getUserByUsername("fulano", realmModel);

        assertThat(userModel).isNull();
        verify(userDAO).findByUsername("fulano");
    }

    @Test
    public void whenGetUserByEmailThenReturnUserModel() {
        var user = User.builder().build();

        when(userDAO.findByEmail("fulano@mail.com")).thenReturn(Optional.of(user));

        var userModel = provider.getUserByEmail("fulano@mail.com", realmModel);

        assertThat(userModel).isNotNull();

        var decoratedUser = getDecoratedUser(userModel);

        assertThat(decoratedUser).isSameAs(user);

        verify(userDAO).findByEmail("fulano@mail.com");
    }

    @Test
    public void whenGetUserByNonexistentEmailThenReturnNull() {
        when(userDAO.findByEmail("fulano@mail.com")).thenReturn(Optional.empty());

        var userModel = provider.getUserByEmail("fulano@mail.com", realmModel);

        assertThat(userModel).isNull();
        verify(userDAO).findByEmail("fulano@mail.com");
    }

    @Test
    public void whenCheckIfSupportsPasswordCredentialTypeThenReturnTrue() {
        assertThat(provider.supportsCredentialType(PasswordCredentialModel.TYPE)).isTrue();
    }

    @Test
    public void whenCheckIfSupportsOtherCredentialTypeThenReturnFalse() {
        assertThat(provider.supportsCredentialType("other-credential-type")).isFalse();
    }

    @Test
    public void whenCheckIfIsConfiguredForThenReturnTrue() {
        assertThat(provider.isConfiguredFor(realmModel, userModel, "any-credential-type")).isTrue();
        verifyNoInteractions(realmModel);
        verifyNoInteractions(userModel);
    }

    @Test
    public void whenCheckIfCredentialInputIsValidThenReturnTrue() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();

        when(userModel.getId()).thenReturn(keycloakId);
        when(properties.getProperty(PASSWORD_ENCODE_TYPE)).thenReturn(BCRYPT.name());
        when(passwordEncoderFactory.getPasswordEncoder(BCRYPT)).thenReturn(passwordEncoder);
        when(userDAO.findPasswordByUserId("external-id")).thenReturn(Optional.of("any-user-password"));
        when(credentialInput.getChallengeResponse()).thenReturn("any-input-password");
        when(passwordEncoder.matches("any-input-password", "any-user-password")).thenReturn(true);

        var isValid = provider.isValid(realmModel, userModel, credentialInput);

        assertThat(isValid).isTrue();

        verify(userModel).getId();
        verify(properties).getProperty(PASSWORD_ENCODE_TYPE);
        verify(passwordEncoderFactory).getPasswordEncoder(BCRYPT);
        verify(userDAO).findPasswordByUserId("external-id");
        verify(credentialInput).getChallengeResponse();
        verify(passwordEncoder).matches("any-input-password", "any-user-password");
    }

    @Test
    public void whenValidateIncorrectCredentialThenReturnFalse() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();

        when(userModel.getId()).thenReturn(keycloakId);
        when(properties.getProperty(PASSWORD_ENCODE_TYPE)).thenReturn(BCRYPT.name());
        when(passwordEncoderFactory.getPasswordEncoder(BCRYPT)).thenReturn(passwordEncoder);
        when(userDAO.findPasswordByUserId("external-id")).thenReturn(Optional.of("any-user-password"));
        when(credentialInput.getChallengeResponse()).thenReturn("any-input-password");
        when(passwordEncoder.matches("any-input-password", "any-user-password")).thenReturn(false);

        var isValid = provider.isValid(realmModel, userModel, credentialInput);

        assertThat(isValid).isFalse();

        verify(userModel).getId();
        verify(properties).getProperty(PASSWORD_ENCODE_TYPE);
        verify(passwordEncoderFactory).getPasswordEncoder(BCRYPT);
        verify(userDAO).findPasswordByUserId("external-id");
        verify(credentialInput).getChallengeResponse();
        verify(passwordEncoder).matches("any-input-password", "any-user-password");
    }

    @Test
    public void whenCheckIfCredentialInputIsValidWithNonexistentUserIdThenReturnFalse() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();

        when(userModel.getId()).thenReturn(keycloakId);
        when(properties.getProperty(PASSWORD_ENCODE_TYPE)).thenReturn(BCRYPT.name());
        when(passwordEncoderFactory.getPasswordEncoder(BCRYPT)).thenReturn(passwordEncoder);
        when(userDAO.findPasswordByUserId("external-id")).thenReturn(Optional.empty());

        var isValid = provider.isValid(realmModel, userModel, credentialInput);

        assertThat(isValid).isFalse();

        verify(userModel).getId();
        verify(properties).getProperty(PASSWORD_ENCODE_TYPE);
        verify(passwordEncoderFactory).getPasswordEncoder(BCRYPT);
        verify(userDAO).findPasswordByUserId("external-id");
        verifyNoInteractions(credentialInput);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void whenOccursAnyExceptionCheckingCredentialInputThenReturnFalse() {
        var keycloakId = new StorageId("provider-id", "external-id").getId();

        when(userModel.getId()).thenReturn(keycloakId);
        when(properties.getProperty(PASSWORD_ENCODE_TYPE)).thenReturn(BCRYPT.name());
        when(passwordEncoderFactory.getPasswordEncoder(BCRYPT)).thenReturn(passwordEncoder);
        when(userDAO.findPasswordByUserId("external-id")).thenThrow(RuntimeException.class);

        var isValid = provider.isValid(realmModel, userModel, credentialInput);

        assertThat(isValid).isFalse();

        verify(userModel).getId();
        verify(properties).getProperty(PASSWORD_ENCODE_TYPE);
        verify(passwordEncoderFactory).getPasswordEncoder(BCRYPT);
        verify(userDAO).findPasswordByUserId("external-id");
        verifyNoInteractions(credentialInput);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void whenCloseDoNothing() {
        provider.close();
        verifyNoInteractions(userModel);
        verifyNoInteractions(properties);
        verifyNoInteractions(passwordEncoderFactory);
        verifyNoInteractions(userDAO);
        verifyNoInteractions(credentialInput);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(session);
        verifyNoInteractions(componentModel);
        verifyNoInteractions(realmModel);
    }

    private <T> T getDecoratedUser(UserModel userModel) {
        try {
            var field = userModel.getClass().getDeclaredField("user");
            field.setAccessible(true);
            return (T) field.get(userModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
