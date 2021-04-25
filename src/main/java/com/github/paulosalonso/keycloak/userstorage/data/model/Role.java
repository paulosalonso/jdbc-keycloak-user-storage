package com.github.paulosalonso.keycloak.userstorage.data.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Role {
    private String id;
    private String name;
    private String description;
}
