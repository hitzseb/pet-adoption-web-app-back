package com.nocountry.petadoptapi.repository;

import com.nocountry.petadoptapi.model.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdopterRepository extends JpaRepository<Adopter, Integer> {
}
