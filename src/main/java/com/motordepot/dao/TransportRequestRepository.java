package com.motordepot.dao;

import com.motordepot.model.TransportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRequestRepository extends JpaRepository<TransportRequest, Integer> {
}