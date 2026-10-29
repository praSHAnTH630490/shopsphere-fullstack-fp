package com.store.service;

import com.store.dao.RoleDAO;
import com.store.dao.UserDAO;
import com.store.dto.auth.AuthResponse;
import com.store.dto.auth.RegisterRequest;
import com.store.entity.Role;
import com.store.entity.User;
import com.store.entity.enums.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RoleDAO roleDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register - successfully registers customer and returns JWT")
    void register_whenNewEmail_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest(
                "Jane Doe",
                "jane@example.com",
                "Password@123",
                "9876543210"
        );

        Role customerRole = new Role(RoleName.CUSTOMER);
        ReflectionTestUtils.setField(customerRole, "id", 1L);

        when(userDAO.existsByEmail("jane@example.com")).thenReturn(false);
        when(roleDAO.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPasswordHash");

        when(userDAO.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 10L);
            u.setRoles(Set.of(customerRole));
            return u;
        });

        when(jwtService.generateToken(any(User.class))).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked.jwt.token");
        assertThat(response.user().email()).isEqualTo("jane@example.com");
        assertThat(response.user().roles()).contains("CUSTOMER");
        verify(userDAO).save(any(User.class));
    }

    @Test
    @DisplayName("register - throws 400 when email already registered")
    void register_whenEmailExists_shouldThrowException() {
        RegisterRequest request = new RegisterRequest(
                "Jane Doe",
                "existing@example.com",
                "Password@123",
                "9876543210"
        );

        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already registered");

        verify(userDAO, never()).save(any());
    }
}
