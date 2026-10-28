package com.motordepot.dao;

import com.motordepot.model.Trip;
import com.motordepot.model.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

public interface TripRepository extends JpaRepository<Trip, Integer> {

    boolean existsByRequest_IdAndStatusNot(Integer id, TripStatus tripStatus);

    boolean existsByDriver_IdAndStatusIn(Integer id, List<TripStatus> activeStatuses);

    boolean existsByAuto_IdAndStatusIn(Integer id, List<TripStatus> activeStatuses);

    @EntityGraph(attributePaths = {"request", "driver", "auto"})
    List<Trip> findAllByStatus(TripStatus status);

    @Override
    @EntityGraph(attributePaths = {"request", "driver", "auto"})
    List<Trip> findAll();
}