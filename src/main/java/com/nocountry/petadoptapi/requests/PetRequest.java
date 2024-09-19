package com.nocountry.petadoptapi.requests;

import com.nocountry.petadoptapi.model.Gender;
import com.nocountry.petadoptapi.model.Size;
import com.nocountry.petadoptapi.model.Species;

public record PetRequest(
        String name,
        Species species,
        Gender gender,
        Integer age,
        String color,
        Size size,
        String image,
        String description
) {
}
