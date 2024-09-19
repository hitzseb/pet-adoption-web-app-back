package com.nocountry.petadoptapi.service;

import com.nocountry.petadoptapi.model.Adopter;
import com.nocountry.petadoptapi.model.Pet;
import com.nocountry.petadoptapi.model.Role;
import com.nocountry.petadoptapi.model.Shelter;
import com.nocountry.petadoptapi.model.User;
import com.nocountry.petadoptapi.repository.AdopterRepository;
import com.nocountry.petadoptapi.repository.PetRepository;
import com.nocountry.petadoptapi.requests.PetRequest;
import com.nocountry.petadoptapi.responses.PetAdopterResponse;
import com.nocountry.petadoptapi.responses.PetResponse;
import com.nocountry.petadoptapi.responses.PetResponseForAdopters;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final AdopterRepository adopterRepository;
    private final UserService userService;
    private final ClassConverter classConverter;

    public PetService(PetRepository petRepository, AdopterRepository adopterRepository, UserService userService, ClassConverter classConverter) {
        this.petRepository = petRepository;
        this.adopterRepository = adopterRepository;
        this.userService = userService;
        this.classConverter = classConverter;
    }

    // Obtener todos los pets y convertirlos a DTO
    public Set<PetResponse> getAllPets() {
        User user = (User) userService.getAuthenticatedUser();
        Role activeRole = user.getActiveRole();
        if (activeRole.equals(Role.ADOPTER)) {
            return getPetResponseForAdopters(user.getAdopterProfile());
        }
        if (activeRole.equals(Role.SHELTER) || activeRole.equals(Role.ADMIN)) {
            return getPetResponseForShelter(user.getShelterProfile());
        }
        return petRepository.findAll().stream()
                .map(classConverter::convertToPetResponse)
                .collect(Collectors.toSet());
    }

    public Set<PetResponse> getShelterPets(Integer id) {
        return petRepository.findAllByShelterId(id).stream()
                .map(classConverter::convertToPetResponse)
                .collect(Collectors.toSet());
    }

    // Obtener un pet por ID y convertirlo a DTO
    public PetResponse getPetById(Integer id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        User user = (User) userService.getAuthenticatedUser();
        Role activeRole = user.getActiveRole();
        if (activeRole.equals(Role.ADOPTER)) {
            return classConverter.convertToPetResponseForAdopter(pet, user.getAdopterProfile());
        }
        if (activeRole.equals(Role.SHELTER) || activeRole.equals(Role.ADMIN)) {
            return classConverter.convertToPetResponseForShelter(pet);
        }
        return classConverter.convertToPetResponse(pet);
    }

    // Guardar un nuevo pet utilizando un DTO
    public PetResponse savePet(PetRequest petRequest) {
        Pet pet = classConverter.convertToPet(petRequest);
        Pet savedPet = petRepository.save(pet);
        return classConverter.convertToPetResponse(savedPet);
    }

    // Actualizar un pet existente por su ID
    public PetResponse updatePet(Integer id, PetRequest petRequest) throws AccessDeniedException {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        User user = (User) userService.getAuthenticatedUser();
        Shelter shelter = user.getShelterProfile();

        if (shelter == null) {
            throw new EntityNotFoundException("El usuario no tiene un refugio asignado");
        }

        if (existingPet.getShelter() == null || !Objects.equals(existingPet.getShelter().getId(), shelter.getId())) {
            throw new AccessDeniedException("Pet doesn't belong to user.");
        }

        // Actualizar los campos del pet existente
        existingPet.setName(petRequest.name());
        existingPet.setSpecies(petRequest.species());
        existingPet.setGender(petRequest.gender());
        existingPet.setAge(petRequest.age());
        existingPet.setColor(petRequest.color());
        existingPet.setSize(petRequest.size());
        existingPet.setImage(petRequest.image());
        existingPet.setDescription(petRequest.description());

        Pet updatedPet = petRepository.save(existingPet);
        return classConverter.convertToPetResponse(updatedPet);
    }

    // Eliminar un pet por su ID
    public void deletePet(Integer id) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        User user = (User) userService.getAuthenticatedUser();
        Shelter shelter = user.getShelterProfile();

        if (shelter == null) {
            throw new EntityNotFoundException("El usuario no tiene un refugio asignado");
        }

        if (existingPet.getShelter() == null || !existingPet.getShelter().equals(shelter)) {
            throw new AccessDeniedException("Pet doesn't belong to user.");
        }
        petRepository.deleteById(id);
    }

    // Solicitud de adopcion
    @Transactional
    public PetAdopterResponse adoptOrCancelPet(Integer id) {
        User user = (User) userService.getAuthenticatedUser();
        Adopter adopter = user.getAdopterProfile();

        if (adopter == null) {
            throw new EntityNotFoundException("El adoptante no tiene un perfil de adoptador.");
        }

        Pet pet = petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Set<Pet> wishList = adopter.getWishList();
        Set<Adopter> interestedAdopters = pet.getInterestedAdopters();

        if (wishList.contains(pet) || interestedAdopters.contains(adopter)) {
            wishList.remove(pet);
            interestedAdopters.remove(adopter);
        } else {
            wishList.add(pet);
            interestedAdopters.add(adopter);
        }

        adopterRepository.save(adopter);
        petRepository.save(pet);

        return new PetAdopterResponse(
                pet.getId(),
                adopter.getId()
        );
    }

    // Trae todas las mascotas en el formato adecuado para adoptantes
    public Set<PetResponse> getPetResponseForAdopters(Adopter adopter) {
        return petRepository.findAll().stream()
                .map(pet -> classConverter.convertToPetResponseForAdopter(pet, adopter))
                .collect(Collectors.toSet());
    }

    // Trae solo las mascotas del refugio en el formato adecuado para refugios
    public Set<PetResponse> getPetResponseForShelter(Shelter shelter) {
        return petRepository.findAllByShelterId(shelter.getId()).stream()
                .map(classConverter::convertToPetResponseForShelter)
                .collect(Collectors.toSet());
    }
}