package com.nocountry.petadoptapi.controller;

import com.nocountry.petadoptapi.model.Adopter;
import com.nocountry.petadoptapi.responses.AdopterResponse;
import com.nocountry.petadoptapi.service.AdopterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/adopter")
public class AdopterController {
    private final AdopterService adopterService;

    public AdopterController(AdopterService adopterService) {
        this.adopterService = adopterService;
    }

    @GetMapping
    public ResponseEntity<?> getAdopter() {
        try {
            Adopter adopter = adopterService.getAdopter();
            AdopterResponse response = new AdopterResponse(
                    adopter.getFirstName(),
                    adopter.getLastName(),
                    adopter.getAddress(),
                    adopter.getContact(),
                    adopter.getDescription()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAdopter(@RequestBody AdopterResponse adopterResponse) {
        try {
            String jwt = adopterService.saveAdopter(adopterResponse);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAdopter(@RequestBody AdopterResponse adopterResponse) {
        try {
            Adopter adopter = adopterService.updateAdopter(adopterResponse);
            AdopterResponse response = new AdopterResponse(
                    adopter.getFirstName(),
                    adopter.getLastName(),
                    adopter.getAddress(),
                    adopter.getContact(),
                    adopter.getDescription()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
