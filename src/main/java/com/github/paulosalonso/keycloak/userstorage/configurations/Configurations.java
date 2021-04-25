package com.github.paulosalonso.keycloak.userstorage.configurations;

public final class Configurations {

    public static final String JDBC_URL = "jdbcUrl";
    public static final String DB_USER = "dataSource.user";
    public static final String DB_PASSWORD = "dataSource.password";
    public static final String USER_QUERY = "user.query.value";
    public static final String USER_ID_FIELD = "user.id.field";
    public static final String USER_USERNAME_FIELD = "user.username.field";
    public static final String USER_EMAIL_FIELD = "user.email.field";
    public static final String USER_PASSWORD_FIELD = "user.password.field";
    public static final String PASSWORD_ENCODE_TYPE = "password.encode.type";
    public static final String ROLE_QUERY = "role.query.value";
    public static final String ROLE_ID_FIELD = "role.id.field";
    public static final String ROLE_USER_ID_FIELD = "role.userid.field";
    public static final String ROLE_NAME_FIELD = "role.name.field";
    public static final String ROLE_DESCRIPTION_FIELD = "role.description.field";

    private Configurations() {}

}
