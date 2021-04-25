package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.model.Role;
import com.github.paulosalonso.keycloak.userstorage.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserModelTest {

    @InjectMocks
    private CustomUserModel customUserModel;

    @Mock
    private KeycloakSession session;

    @Mock
    private RealmModel realm;

    @Mock
    private ComponentModel storageProviderModel;

    @Mock
    private User user;

    @Mock
    private List<Role> roles;

    @Test
    public void whenGetIdThenReturnKeycloakStorageIdFormat() {
        when(storageProviderModel.getId()).thenReturn("storage-id");
        when(user.getId()).thenReturn("user-id");
        assertThat(customUserModel.getId()).isEqualTo(new StorageId("storage-id", "user-id").getId());
        verify(user).getId();
    }

    @Test
    public void whenGetUsernameThenReturnDecoratedUserUsername() {
        when(user.getUsername()).thenReturn("decorated-user-username");
        assertThat(customUserModel.getUsername()).isEqualTo("decorated-user-username");
        verify(user).getUsername();
    }

    @Test
    public void whenGetEmailThenReturnDecoratedUserEmail() {
        when(user.getEmail()).thenReturn("decorated-user-email");
        assertThat(customUserModel.getEmail()).isEqualTo("decorated-user-email");
        verify(user).getEmail();
    }

    @Test
    public void whenGetRoleMappingsInternalThenReturnMappedRoles() {
        var role = Role.builder()
                .id("id")
                .name("name")
                .description("description")
                .build();

        var userModel = new CustomUserModel(session, realm, storageProviderModel, user, List.of(role));
        var mappedRoles = userModel.getRoleMappingsInternal();
        assertThat(mappedRoles)
                .hasSize(1)
                .first()
                .isNotNull();
    }

//    @Test
//    public void whenGetRoleMappingsInternalThenReturnEmptyList() {
//        var userModel = new CustomUserModel(session, realm, storageProviderModel, user, Collections.emptyList());
//        var mappedRoles = userModel.getRoleMappingsInternal();
//        assertThat(mappedRoles).isEmpty();
//    }
}
