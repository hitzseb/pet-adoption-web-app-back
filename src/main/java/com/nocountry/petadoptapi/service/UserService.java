package com.nocountry.petadoptapi.service;

import com.nocountry.petadoptapi.model.Role;
import com.nocountry.petadoptapi.requests.AuthRequest;
import com.nocountry.petadoptapi.exceptions.UserAlreadyExistsException;
import com.nocountry.petadoptapi.model.User;
import com.nocountry.petadoptapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetails;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, UserDetailsServiceImpl userDetails, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userDetails = userDetails;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public boolean existsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    public void registerUser(AuthRequest authRequest) throws UserAlreadyExistsException {
        String email = authRequest.email();
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }
        String password = passwordEncoder.encode(authRequest.password());
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Set<Role> roles = user.getRoles();
        roles.add(Role.USER);
        user.setRoles(roles);
        userRepository.save(user);
    }

    public String authenticate(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails user = userDetails.loadUserByUsername(authRequest.email());
        return jwtUtil.generateToken(user);
    }

    public String switchRole(Role newActiveRole) {
        User user = (User) getAuthenticatedUser();
        if (newActiveRole == null) {
            throw new IllegalArgumentException("New active role cannot be null");
        }
        if (!user.getRoles().contains(newActiveRole)) {
            throw new IllegalArgumentException("Invalid role: " + newActiveRole);
        }
        user.setActiveRole(newActiveRole);
        userRepository.save(user);
        return jwtUtil.generateToken(user);
    }

    public UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
}
