package com.github.paulosalonso.keycloak.userstorage.data.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private final String id;
    private final String username;
    private final String email;
}
