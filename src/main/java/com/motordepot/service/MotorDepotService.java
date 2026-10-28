package com.motordepot.service;

import com.motordepot.model.MotorDepot;
import com.motordepot.dao.MotorDepotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MotorDepotService {

    private final MotorDepotRepository motorDepotRepository;

    @Autowired
    public MotorDepotService(MotorDepotRepository motorDepotRepository) {
        this.motorDepotRepository = motorDepotRepository;
    }

    public MotorDepot getDepot() {
        List<MotorDepot> depots = motorDepotRepository.findAll();

        if (depots.isEmpty()) {
            throw new IllegalStateException("Автобаза не создана. Выполните начальное заполнение базы");
        }

        if (depots.size() > 1) {
            throw new IllegalStateException("В системе должна существовать только одна автобаза");
        }

        return depots.getFirst();
    }
}