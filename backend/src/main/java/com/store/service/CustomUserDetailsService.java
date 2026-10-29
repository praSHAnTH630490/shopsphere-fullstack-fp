package com.store.service;

import com.store.dao.UserDAO;
import com.store.entity.Role;
import com.store.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.store.entity.User;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userDAO.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .map(role -> new SimpleGrantedAuthority(
                                        "ROLE_" + role.name()
                                ))
                                .collect(Collectors.toSet())
                )
                .disabled(!user.isActive())
                .build();
    }
    
    public User getUser(String email) {
    return userDAO.findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found")
            );
}
}