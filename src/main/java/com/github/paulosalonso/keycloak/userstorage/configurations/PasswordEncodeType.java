package com.github.paulosalonso.keycloak.userstorage.configurations;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum PasswordEncodeType {
    BCRYPT,
    MD5,
    NONE;

    public static List<String> asStringList() {
        return Arrays.stream(values()).map(Enum::name).collect(toList());
    }

    public static PasswordEncodeType of(String name) {
        for (var type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid password encode type: " + name);
    }
}
