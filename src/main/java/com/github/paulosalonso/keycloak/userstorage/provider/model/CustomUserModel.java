package com.github.paulosalonso.keycloak.userstorage.provider.model;

import com.github.paulosalonso.keycloak.userstorage.data.model.Role;
import com.github.paulosalonso.keycloak.userstorage.data.model.User;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CustomUserModel extends AbstractUserAdapter {

    private final User user;
    private final Set<RoleModel> roles;

    public CustomUserModel(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, User user, List<Role> roles) {
        super(session, realm, storageProviderModel);
        this.user = user;
        this.roles = roles.stream()
                .map(role -> new CustomRoleModel(role, realm))
                .collect(Collectors.toSet());
    }

    @Override
    public String getId() {
        return new StorageId(storageProviderModel.getId(), user.getId()).getId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Set<RoleModel> getRoleMappingsInternal() {
        return roles;
    }
}
