package com.motordepot.service;

import com.motordepot.model.MotorDepot;
import com.motordepot.model.TransportRequest;
import com.motordepot.model.enums.CargoType;
import com.motordepot.model.enums.RequestStatus;
import com.motordepot.dao.TransportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TransportRequestService {

    private final TransportRequestRepository transportRequestRepository;
    private final MotorDepotService motorDepotService;

    @Autowired
    public TransportRequestService(TransportRequestRepository transportRequestRepository, MotorDepotService motorDepotService) {
        this.transportRequestRepository = transportRequestRepository;
        this.motorDepotService = motorDepotService;
    }

    public List<TransportRequest> findAll() {
        return transportRequestRepository.findAll();
    }

    public TransportRequest findById(Integer id) {
        return transportRequestRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                        "Заявка с ID " + id + " не найдена"));
    }

    @Transactional
    public TransportRequest create(String destination, double cargoWeightKg, CargoType cargoType) {

        validateRequestData(destination, cargoWeightKg, cargoType);
        MotorDepot depot = motorDepotService.getDepot();
        TransportRequest request = transportRequestRepository.save(new TransportRequest(destination.trim(), cargoWeightKg, cargoType)
        );

        depot.getTransportRequests().add(request);
        return request;
    }

    @Transactional
    public TransportRequest update(Integer id, String destination, double cargoWeightKg, CargoType cargoType) {

        validateRequestData(destination, cargoWeightKg, cargoType);

        TransportRequest request = findById(id);
        requireNewStatus(request);

        request.setDestination(destination.trim());
        request.setCargoWeightKg(cargoWeightKg);
        request.setCargoType(cargoType);

        return request;
    }

    @Transactional
    public TransportRequest cancel(Integer id) {
        TransportRequest request = findById(id);
        requireNewStatus(request);
        request.setRequestStatus(RequestStatus.CANCELLED);
        return request;
    }

    private void requireNewStatus(TransportRequest request) {
        if (request.getRequestStatus() != RequestStatus.NEW) {
            throw new IllegalStateException("Изменить или отменить можно только новую заявку");
        }
    }

    private void validateRequestData(String destination, double cargoWeightKg, CargoType cargoType) {

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Пункт назначения не должен быть пустым");
        }

        if (destination.trim().length() > 200) {
            throw new IllegalArgumentException("Пункт назначения не должен превышать 200 символов");
        }

        if (!Double.isFinite(cargoWeightKg) || cargoWeightKg <= 0) {
            throw new IllegalArgumentException("Вес груза должен быть конечным числом больше нуля");
        }

        if (cargoType == null) {
            throw new IllegalArgumentException("Необходимо указать тип груза");
        }
    }
}