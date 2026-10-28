package com.motordepot.service;

import com.motordepot.model.Auto;
import com.motordepot.model.Driver;
import com.motordepot.model.TransportRequest;
import com.motordepot.model.Trip;
import com.motordepot.model.enums.RequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispatcherService {

    private final TransportRequestService transportRequestService;
    private final DriverService driverService;
    private final AutoService autoService;
    private final TripService tripService;

    @Autowired
    public DispatcherService(TransportRequestService transportRequestService, DriverService driverService,
            AutoService autoService, TripService tripService) {

        this.transportRequestService = transportRequestService;
        this.driverService = driverService;
        this.autoService = autoService;
        this.tripService = tripService;
    }
    //создаем рейс
    @Transactional
    public Trip assignTrip(Integer requestId, double transportationCost) {

        if (transportationCost < 0) {
            throw new IllegalArgumentException("Стоимость перевозки должна быть положительной");
        }

        TransportRequest request = transportRequestService.findById(requestId);

        if (request.getRequestStatus() != RequestStatus.NEW) {
            throw new IllegalStateException("Назначить рейс можно только для новой заявки");
        }

        Auto auto = autoService.findAvailableAuto(request.getCargoType(), request.getCargoWeightKg());
        Driver driver = driverService.findAvailableDriver(request.getCargoType());

        return tripService.createTrip(request, driver, auto, transportationCost);
    }
}