package com.github.paulosalonso.keycloak.userstorage.provider.model;

import com.github.paulosalonso.keycloak.userstorage.data.model.Role;
import lombok.RequiredArgsConstructor;
import org.keycloak.models.RoleContainerModel;

@RequiredArgsConstructor
public class CustomRoleModel extends AbstractRoleModel {

    private final Role role;
    private final RoleContainerModel container;

    @Override
    public String getId() {
        return role.getId();
    }

    @Override
    public String getName() {
        return role.getName();
    }

    @Override
    public String getDescription() {
        return role.getDescription();
    }

    @Override
    public RoleContainerModel getContainer() {
        return container;
    }
}
