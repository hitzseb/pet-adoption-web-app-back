package com.nocountry.petadoptapi.service;

import com.nocountry.petadoptapi.requests.ShelterRequest;
import com.nocountry.petadoptapi.model.Role;
import com.nocountry.petadoptapi.model.Shelter;
import com.nocountry.petadoptapi.model.User;
import com.nocountry.petadoptapi.repository.ShelterRepository;
import com.nocountry.petadoptapi.repository.UserRepository;
import com.nocountry.petadoptapi.responses.ShelterResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ClassConverter classConverter;

    public ShelterService(ShelterRepository shelterRepository, UserRepository userRepository, UserService userService, JwtUtil jwtUtil, ClassConverter classConverter) {
        this.shelterRepository = shelterRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.classConverter = classConverter;
    }

    public Shelter getMyShelter() {
        UserDetails userDetails = userService.getAuthenticatedUser();
        User user = (User) userDetails;
        if (user.getShelterProfile() == null) {
            throw new IllegalStateException("User has no shelter profile.");
        }
        return user.getShelterProfile();
    }

    public ShelterResponse getShelterById(Integer id) {
        Shelter shelter = shelterRepository.findById(id).orElseThrow(() -> new RuntimeException("No shelter found with id " + id));
        return classConverter.convertToShelterResponse(shelter);
    }

    public Set<ShelterResponse> getAllShelters() {
        return shelterRepository.findAll().stream()
                .map(classConverter::convertToShelterResponse)
                .collect(Collectors.toSet());
    }

    public String saveShelter(ShelterRequest shelterRequest) {
        UserDetails userDetails = userService.getAuthenticatedUser();
        User user = (User) userDetails;

        if (user.getShelterProfile() != null) {
            throw new IllegalStateException("User already has a shelter profile.");
        }

        Shelter shelter = new Shelter();
        shelter.setShelterName(shelterRequest.shelterName());
        shelter.setImage(shelterRequest.image());
        shelter.setAddress(shelterRequest.address());
        shelter.setContact(shelterRequest.contact());
        shelter.setDescription(shelterRequest.description());
        user.setShelterProfile(shelter);
        Set<Role> roles = user.getRoles();
        roles.add(Role.SHELTER);
        user.setRoles(roles);
        user.setActiveRole(Role.SHELTER);
        userRepository.save(user);
        return jwtUtil.generateToken(user);
    }

    public Shelter updateShelter(ShelterRequest shelterRequest) {
        UserDetails userDetails = userService.getAuthenticatedUser();
        User user = (User) userDetails;
        Integer shelterId = user.getShelterProfile().getId();

        if (user.getShelterProfile() == null || !Objects.equals(user.getAdopterProfile().getId(), shelterId)) {
            throw new IllegalStateException("User does not have a shelter profile or the profile ID does not match.");
        }

        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found with ID: " + shelterId));

        shelter.setShelterName(shelterRequest.shelterName());
        shelter.setImage(shelterRequest.image());
        shelter.setAddress(shelterRequest.address());
        shelter.setContact(shelterRequest.contact());
        shelter.setDescription(shelterRequest.description());

        return shelterRepository.save(shelter);
    }
}
