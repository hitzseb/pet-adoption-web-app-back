package com.nocountry.petadoptapi.repository;

import com.nocountry.petadoptapi.model.Adopter;
import com.nocountry.petadoptapi.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    Set<Pet> findAllByShelterId(Integer shelterId);

    @Query("SELECT p FROM Pet p JOIN p.interestedAdopters a WHERE a.id = :adopterId")
    Set<Pet> findAllByAdopterWishList(@Param("adopterId") Integer adopterId);

    @Query("SELECT a FROM Adopter a JOIN a.wishList p WHERE p.id = :petId")
    Set<Adopter> findAllInterestedAdopters(@Param("petId") Integer petId);

}
