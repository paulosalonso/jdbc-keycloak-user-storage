package com.github.paulosalonso.keycloak.userstorage.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class User {
    private final String id;
    private final String username;
    private final String email;
    private final String password;
}
