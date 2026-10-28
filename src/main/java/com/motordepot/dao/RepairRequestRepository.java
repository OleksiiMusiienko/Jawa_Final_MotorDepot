package com.motordepot.dao;

import com.motordepot.model.RepairRequest;
import com.motordepot.model.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Collection;
import java.util.List;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, Integer> {
    boolean existsByAuto_IdAndStatusIn(Integer id, Collection<RepairStatus> openRepairStatuses);
    @Override
    //згрузка связанных обьектов
    @EntityGraph(attributePaths = {"auto", "driver", "trip"})
    List<RepairRequest> findAll();
}