package com.nocountry.petadoptapi.service;

import com.nocountry.petadoptapi.model.Adopter;
import com.nocountry.petadoptapi.model.Pet;
import com.nocountry.petadoptapi.model.Shelter;
import com.nocountry.petadoptapi.model.User;
import com.nocountry.petadoptapi.repository.PetRepository;
import com.nocountry.petadoptapi.requests.PetRequest;
import com.nocountry.petadoptapi.responses.AdopterResponse;
import com.nocountry.petadoptapi.responses.PetResponseForAdopters;
import com.nocountry.petadoptapi.responses.PetResponseForShelter;
import com.nocountry.petadoptapi.responses.PetResponseImpl;
import com.nocountry.petadoptapi.responses.ShelterResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassConverter {
    private final UserService userService;
    private final PetRepository petRepository;

    public ClassConverter(UserService userService, PetRepository petRepository) {
        this.userService = userService;
        this.petRepository = petRepository;
    }

    private AdopterResponse convertToAdopterResponse(Adopter adopter) {
        return new AdopterResponse(
                adopter.getFirstName(),
                adopter.getLastName(),
                adopter.getAddress(),
                adopter.getContact(),
                adopter.getDescription()
        );
    }

    ShelterResponse convertToShelterResponse(Shelter shelter) {
        return new ShelterResponse(
                shelter.getId(),
                shelter.getShelterName(),
                shelter.getImage(),
                shelter.getAddress(),
                shelter.getContact(),
                shelter.getDescription(),
                petRepository.findAllByShelterId(shelter.getId()).stream()
                        .map(this::convertToPetResponse)
                        .collect(Collectors.toSet())
        );
    }

    // Conversión de Entity a DTO
    PetResponseImpl convertToPetResponse(Pet pet) {
        return new PetResponseImpl(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getGender(),
                pet.getAge(),
                pet.getColor(),
                pet.getSize(),
                pet.getImage(),
                pet.getDescription(),
                pet.getShelter().getId()
        );
    }

    PetResponseForAdopters convertToPetResponseForAdopter(Pet pet, Adopter adopter) {
        Set<Adopter> interestedAdopters = pet.getInterestedAdopters();
        return new PetResponseForAdopters(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getGender(),
                pet.getAge(),
                pet.getColor(),
                pet.getSize(),
                pet.getImage(),
                pet.getDescription(),
                pet.getShelter().getId(),
                pet.getShelter().getShelterName(),
                pet.getShelter().getContact(),
                interestedAdopters.contains(adopter)
        );
    }

    PetResponseForShelter convertToPetResponseForShelter(Pet pet) {
        Set<Adopter> interestedAdopters = pet.getInterestedAdopters();
        return new PetResponseForShelter(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getGender(),
                pet.getAge(),
                pet.getColor(),
                pet.getSize(),
                pet.getImage(),
                pet.getDescription(),
                interestedAdopters.size(),
                interestedAdopters.stream()
                        .map(this::convertToAdopterResponse)
                        .collect(Collectors.toSet())
        );
    }

    // Conversión de DTO a Entity
    Pet convertToPet(PetRequest petRequest) {
        Pet pet = new Pet();
        pet.setName(petRequest.name());
        pet.setSpecies(petRequest.species());
        pet.setGender(petRequest.gender());
        pet.setAge(petRequest.age());
        pet.setColor(petRequest.color());
        pet.setSize(petRequest.size());
        pet.setImage(petRequest.image());
        pet.setDescription(petRequest.description());

        User user = (User) userService.getAuthenticatedUser();
        Shelter shelter = user.getShelterProfile();
        if (shelter == null) {
            throw new RuntimeException("El usuario no tiene un refugio asignado");
        }
        pet.setShelter(shelter);
        return pet;
    }
}
