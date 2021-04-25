package com.github.paulosalonso.keycloak.userstorage.provider;

import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractRoleModel implements RoleModel {

    @Override
    public abstract String getId();

    @Override
    public abstract String getName();

    @Override
    public abstract String getDescription();

    @Override
    public abstract RoleContainerModel getContainer();

    @Override
    public void setDescription(String description) {}

    @Override
    public void setName(String name) {}

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void addCompositeRole(RoleModel role) {}

    @Override
    public void removeCompositeRole(RoleModel role) {}

    @Override
    public Stream<RoleModel> getCompositesStream() {
        return null;
    }

    @Override
    public boolean isClientRole() {
        return false;
    }

    @Override
    public String getContainerId() {
        return null;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return false;
    }

    @Override
    public void setSingleAttribute(String name, String value) {}

    @Override
    public void setAttribute(String name, List<String> values) {}

    @Override
    public void removeAttribute(String name) {}

    @Override
    public Stream<String> getAttributeStream(String name) {
        return null;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return null;
    }
}
