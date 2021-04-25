package com.github.paulosalonso.keycloak.userstorage.provider;

import com.github.paulosalonso.keycloak.userstorage.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.RoleContainerModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomRoleModelTest {

    @InjectMocks
    private CustomRoleModel customRoleModel;

    @Mock
    private Role role;

    @Mock
    private RoleContainerModel container;

    @Test
    public void whenGetIdThenCallDecoratedRoleIdGetter() {
        when(role.getId()).thenReturn("role-id");
        assertThat(customRoleModel.getId()).isEqualTo("role-id");
        verify(role).getId();
    }

    @Test
    public void whenGetNameThenCallDecoratedRoleNameGetter() {
        when(role.getName()).thenReturn("role-name");
        assertThat(customRoleModel.getName()).isEqualTo("role-name");
        verify(role).getName();
    }

    @Test
    public void whenGetDescriptionThenCallDecoratedRoleDescriptionGetter() {
        when(role.getDescription()).thenReturn("role-description");
        assertThat(customRoleModel.getDescription()).isEqualTo("role-description");
        verify(role).getDescription();
    }

    @Test
    public void whenGetContainerThenReturnContainerSuppliedInConstructor() {
        assertThat(customRoleModel.getContainer()).isSameAs(container);
    }

}
