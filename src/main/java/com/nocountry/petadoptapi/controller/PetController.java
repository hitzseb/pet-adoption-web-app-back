package com.nocountry.petadoptapi.controller;

import com.nocountry.petadoptapi.requests.PetRequest;
import com.nocountry.petadoptapi.responses.PetAdopterResponse;
import com.nocountry.petadoptapi.responses.PetResponse;
import com.nocountry.petadoptapi.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // Obtener todos los pets (Devuelve una lista de PetResponse)
    @GetMapping
    public ResponseEntity<?> getAllPets() {
        try {
            Set<PetResponse> response = petService.getAllPets();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    // Obtener un pet por ID (Devuelve un PetDto)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Integer id) {
        try {
            PetResponse pet = petService.getPetById(id);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Crear un nuevo pet (Recibe y devuelve un PetDto)
    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestBody PetRequest petRequest) {
        try {
            PetResponse savedPet = petService.savePet(petRequest);
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Actualizar un pet existente por su ID
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updatePet(@PathVariable Integer id, @RequestBody PetRequest petRequest) {
        try {
            PetResponse updatedPet = petService.updatePet(id, petRequest);
            return ResponseEntity.ok(updatedPet);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    // Eliminar un pet por su ID
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deletePet(@PathVariable Integer id) {
        try {
            petService.deletePet(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/adopt")
    public ResponseEntity<?> adoptPet(@PathVariable Integer id) {
        try {
            PetAdopterResponse response = petService.adoptOrCancelPet(id);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }
}
