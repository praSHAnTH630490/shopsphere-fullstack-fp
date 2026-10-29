package com.store.service;

import com.store.dao.RoleDAO;
import com.store.dao.UserDAO;
import com.store.dto.auth.AuthResponse;
import com.store.dto.auth.LoginRequest;
import com.store.dto.auth.RegisterRequest;
import com.store.dto.auth.UserResponse;
import com.store.entity.Role;
import com.store.entity.User;
import com.store.entity.enums.RoleName;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserDAO userDAO,
            RoleDAO roleDAO,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userDAO.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        Role customerRole = roleDAO.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(RoleName.CUSTOMER);
                    return roleDAO.save(role);
                });

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setPhone(request.phone());
        user.setActive(true);
        user.setRoles(Set.of(customerRole));

        User savedUser = userDAO.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                toUserResponse(savedUser)
        );
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        User user = userDAO.findByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                toUserResponse(user)
        );
    }

    private UserResponse toUserResponse(User user) {

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                roles,
                user.isActive(),
                user.getCreatedAt()
        );
    }
}