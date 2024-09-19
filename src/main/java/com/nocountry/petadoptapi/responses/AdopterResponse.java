package com.nocountry.petadoptapi.responses;

public record AdopterResponse(
        String firstName,
        String lastName,
        String address,
        String contact,
        String description
) {
}
