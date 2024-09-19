package com.nocountry.petadoptapi.requests;

public record ShelterRequest(
        String shelterName,
        String image,
        String address,
        String contact,
        String description
) {
}
