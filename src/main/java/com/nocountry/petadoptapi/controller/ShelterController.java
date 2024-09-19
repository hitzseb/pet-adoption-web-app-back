package com.nocountry.petadoptapi.controller;

import com.nocountry.petadoptapi.requests.ShelterRequest;
import com.nocountry.petadoptapi.model.Shelter;
import com.nocountry.petadoptapi.responses.ShelterResponse;
import com.nocountry.petadoptapi.service.ShelterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/shelter")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<?> getMyShelter() {
        try {
            Shelter shelter = shelterService.getMyShelter();
            ShelterRequest response = new ShelterRequest(
                    shelter.getShelterName(),
                    shelter.getImage(),
                    shelter.getAddress(),
                    shelter.getContact(),
                    shelter.getDescription()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllShelters() {
        try {
            Set<ShelterResponse> response = shelterService.getAllShelters();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createShelter(@RequestBody ShelterRequest shelterRequest) {
        try {
            String jwt = shelterService.saveShelter(shelterRequest);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateShelter(@RequestBody ShelterRequest shelterRequest) {
        try {
            Shelter shelter = shelterService.updateShelter(shelterRequest);
            ShelterRequest response = new ShelterRequest(
                    shelter.getShelterName(),
                    shelter.getImage(),
                    shelter.getAddress(),
                    shelter.getContact(),
                    shelter.getDescription()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
