package com.nocountry.petadoptapi.responses;

import com.nocountry.petadoptapi.model.Gender;
import com.nocountry.petadoptapi.model.Size;
import com.nocountry.petadoptapi.model.Species;

public record PetResponseForAdopters(
        Integer id,
        String name,
        Species species,
        Gender gender,
        Integer age,
        String color,
        Size size,
        String image,
        String description,
        Integer shelterId,
        String shelterName,
        String shelterContact,
        Boolean isOnMyWishlist
) implements PetResponse {
}
