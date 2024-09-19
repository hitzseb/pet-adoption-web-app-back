package com.nocountry.petadoptapi.responses;

import com.nocountry.petadoptapi.model.Gender;
import com.nocountry.petadoptapi.model.Size;
import com.nocountry.petadoptapi.model.Species;

public record PetResponseImpl(
        Integer id,
        String name,
        Species species,
        Gender gender,
        Integer age,
        String color,
        Size size,
        String image,
        String description,
        Integer shelterId
) implements PetResponse {
}
