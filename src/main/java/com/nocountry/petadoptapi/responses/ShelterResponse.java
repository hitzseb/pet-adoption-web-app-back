package com.nocountry.petadoptapi.responses;

import java.util.Set;

public record ShelterResponse(
        Integer id,
        String shelterName,
        String image,
        String address,
        String contact,
        String description,
        Set<PetResponse> pets
) {
}
