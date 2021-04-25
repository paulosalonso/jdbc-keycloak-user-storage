package com.github.paulosalonso.keycloak.userstorage.data.mapper;

import com.github.paulosalonso.keycloak.userstorage.model.Role;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.github.paulosalonso.keycloak.userstorage.configurations.Configurations.*;

@RequiredArgsConstructor
public class RoleMapper {

    private final Properties properties;

    public List<Role> map(ResultSet resultSet) throws SQLException {
        var roles = new ArrayList<Role>();

        while(resultSet.next()) {
            roles.add(Role.builder()
                    .id(resultSet.getString(properties.getProperty(ROLE_ID_FIELD)))
                    .name(resultSet.getString(properties.getProperty(ROLE_NAME_FIELD)))
                    .description(resultSet.getString(properties.getProperty(ROLE_DESCRIPTION_FIELD)))
                    .build());
        }

        return roles;
    }
}
