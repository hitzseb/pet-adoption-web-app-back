package com.nocountry.petadoptapi.repository;

import com.nocountry.petadoptapi.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, Integer> {
}
