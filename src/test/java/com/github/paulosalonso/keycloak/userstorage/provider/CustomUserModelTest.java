package com.github.paulosalonso.keycloak.userstorage.provider;

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
}
