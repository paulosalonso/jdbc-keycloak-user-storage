package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.configurations.PasswordEncodeType;
import com.github.paulosalonso.keycloak.userstorage.data.dao.RoleDAO;
import com.github.paulosalonso.keycloak.userstorage.data.dao.UserDAO;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.PASSWORD_ENCODE_TYPE;

@Slf4j
@RequiredArgsConstructor
public class JdbcUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final KeycloakSession session;
    private final ComponentModel componentModel;
    private final Properties properties;
    private final PasswordEncoderFactory passwordEncoderFactory;

    @Override
    public UserModel getUserById(String id, RealmModel realmModel) {
        id = new StorageId(id).getExternalId();
        log.debug("Find user by external id: {}", id);
        return findUser(userDAO::findById, id, realmModel);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        log.debug("Find user by username: {}", username);
        return findUser(userDAO::findByUsername, username, realmModel);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        log.debug("Find user by email: {}", email);
        return  findUser(userDAO::findByEmail, email, realmModel);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.debug(String.format("Checking credential support for: %s", credentialType));
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return true;
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        var encodeType = PasswordEncodeType.of(properties.getProperty(PASSWORD_ENCODE_TYPE));

        log.debug("Validating credential with credential encoder {}", encodeType);

        var encoder = passwordEncoderFactory.getPasswordEncoder(encodeType);

        try {
            var storageId = new StorageId(userModel.getId());
            log.debug("Searching password for user id: {}", storageId.getExternalId());
            return userDAO.findPasswordByUserId(storageId.getExternalId())
                    .map(password -> encoder.matches(credentialInput.getChallengeResponse(), password))
                    .orElse(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void close() {}

    private <T> UserModel findUser(Function<T, Optional<User>> function, T param, RealmModel realmModel) {
        var opt = function.apply(param)
                .map(user -> new CustomUserModel(session, realmModel, componentModel, user, roleDAO.getRolesByUserId(user.getId())));

        if (opt.isPresent()) {
            log.debug("User found");
            return opt.get();
        } else {
            log.debug("User not found");
            return null;
        }
    }
}
