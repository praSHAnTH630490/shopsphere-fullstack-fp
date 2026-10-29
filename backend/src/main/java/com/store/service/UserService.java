package com.store.service;

import com.store.dao.UserDAO;
import com.store.dto.auth.UserResponse;
import com.store.dto.user.ChangePasswordRequest;
import com.store.dto.user.UpdateProfileRequest;
import com.store.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserDAO userDAO,
            PasswordEncoder passwordEncoder
    ) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {

        return toResponse(getUser(email));
    }

    @Transactional
    public UserResponse updateProfile(
            String email,
            UpdateProfileRequest request
    ) {

        User user = getUser(email);

        user.setName(request.name().trim());
        user.setPhone(
                request.phone() == null
                        ? null
                        : request.phone().trim()
        );

        User savedUser = userDAO.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {

        User user = getUser(email);

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userDAO.save(user);
    }

    private User getUser(String email) {

        return userDAO
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(java.util.stream.Collectors.toSet()),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}