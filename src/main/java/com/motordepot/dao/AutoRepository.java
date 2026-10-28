package com.motordepot.dao;

import com.motordepot.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutoRepository extends JpaRepository<Auto, Integer> {

    Optional<Auto> findByRegistrationNumber(String registrationNumber);
}
