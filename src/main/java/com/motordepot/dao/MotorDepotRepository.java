package com.motordepot.dao;

import com.motordepot.model.MotorDepot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorDepotRepository
        extends JpaRepository<MotorDepot, Integer> {
}