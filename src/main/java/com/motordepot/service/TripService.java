package com.motordepot.service;

import com.motordepot.model.*;
import com.motordepot.model.enums.AutoCondition;
import com.motordepot.model.enums.RequestStatus;
import com.motordepot.model.enums.TripStatus;
import com.motordepot.dao.AutoRepository;
import com.motordepot.dao.DriverRepository;
import com.motordepot.dao.TransportRequestRepository;
import com.motordepot.dao.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TripService {

    private static final List<TripStatus> ACTIVE_STATUS = List.of(TripStatus.PLANNED, TripStatus.IN_PROGRESS, TripStatus.SUSPENDED);

    private final TripRepository tripRepository;
    private final TransportRequestRepository requestRepository;
    private final DriverRepository driverRepository;
    private final AutoRepository autoRepository;
    private final DriverService driverService;
    private final MotorDepotService motorDepotService;

    @Autowired
    public TripService(
            TripRepository tripRepository,
            TransportRequestRepository requestRepository,
            DriverRepository driverRepository,
            AutoRepository autoRepository,
            DriverService driverService,
            MotorDepotService motorDepotService) {

        this.tripRepository = tripRepository;
        this.requestRepository = requestRepository;
        this.driverRepository = driverRepository;
        this.autoRepository = autoRepository;
        this.driverService = driverService;
        this.motorDepotService = motorDepotService;
    }

    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public Trip findById(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Рейс с ID " + id + " не найден"));
    }

    @Transactional
    public Trip createTrip(TransportRequest request, Driver driver, Auto auto, double transportationCost) {

        if (request == null || driver == null || auto == null) {
            throw new IllegalArgumentException("Необходимо передать заявку, водителя и автомобиль");
        }

        if (request.getId() == null || driver.getId() == null || auto.getId() == null) {
            throw new IllegalArgumentException("Переданный параметр не сохранён в базе, ID == null");
        }

        if (transportationCost < 0) {
            throw new IllegalArgumentException("Стоимость перевозки не может быть -");
        }

        TransportRequest savedRequest = requestRepository
                .findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("Заявка не найдена"));

        Driver savedDriver = driverRepository.findById(driver.getId())
                .orElseThrow(() -> new NoSuchElementException("Водитель не найден"));

        Auto savedAuto = autoRepository.findById(auto.getId())
                .orElseThrow(() -> new NoSuchElementException("Автомобиль не найден"));

        validateTripCreation(savedRequest, savedDriver, savedAuto);

        validateAssignment(savedRequest, savedDriver, savedAuto);
        MotorDepot depot = motorDepotService.getDepot();

        Trip trip = tripRepository.save(new Trip(savedRequest, savedDriver, savedAuto, transportationCost));
        depot.getTrips().add(trip);

        savedRequest.setRequestStatus(RequestStatus.ASSIGNED);
        return trip;
    }

    private void validateTripCreation(TransportRequest savedRequest, Driver savedDriver, Auto savedAuto){
        if (savedRequest.getRequestStatus() != RequestStatus.NEW) {
            throw new IllegalStateException("Рейс можно создать только для новой заявки");
        }

        if (tripRepository.existsByRequest_IdAndStatusNot(savedRequest.getId(), TripStatus.CANCELLED)) {
            throw new IllegalStateException("Для заявки уже существует неотменённый рейс");
        }

        if (tripRepository.existsByDriver_IdAndStatusIn(savedDriver.getId(), ACTIVE_STATUS)) {
            throw new IllegalStateException("Водитель уже назначен на активный рейс");
        }

        if (tripRepository.existsByAuto_IdAndStatusIn(savedAuto.getId(), ACTIVE_STATUS)) {
            throw new IllegalStateException("Автомобиль уже назначен на активный рейс");
        }
    }

    @Transactional
    public Trip startTrip(Integer tripId) {

        Trip trip = findById(tripId);

        requireStatus(trip, TripStatus.PLANNED);
        requireAssignedRequest(trip);

        validateAssignment(trip.getRequest(), trip.getDriver(), trip.getAuto());

        trip.setStatus(TripStatus.IN_PROGRESS);

        return trip;
    }

    @Transactional
    public Trip completeTrip(Integer tripId, AutoCondition conditionAtCompletion) {

        if (conditionAtCompletion == null
                || conditionAtCompletion == AutoCondition.UNDER_REPAIR) {
            throw new IllegalArgumentException("Укажите состояние: исправен или требует ремонта");
        }

        Trip trip = findById(tripId);

        requireStatus(trip, TripStatus.IN_PROGRESS);
        requireAssignedRequest(trip);

        if (trip.getAuto().getAutoCondition()
                != AutoCondition.SERVICEABLE) {
            throw new IllegalStateException("Сначала необходимо обработать поломку и возобновить рейс");
        }

        double payment = driverService.calculatePayment(trip.getTransportationCost(), trip.getDriver().getExperienceYears());

        trip.setDriverPayment(payment);
        trip.setAutoConditionAtCompletion(conditionAtCompletion);
        trip.getAuto().setAutoCondition(conditionAtCompletion);

        trip.setStatus(TripStatus.COMPLETED);
        trip.getRequest().setRequestStatus(RequestStatus.COMPLETED);

        return trip;
    }

    @Transactional
    public Trip cancelTrip(Integer tripId) {
        Trip trip = findById(tripId);

        //отменяем до начала перевозки
        requireStatus(trip, TripStatus.PLANNED);
        requireAssignedRequest(trip);

        trip.setStatus(TripStatus.CANCELLED);
        trip.getRequest().setRequestStatus(RequestStatus.NEW);

        return trip;
    }

    @Transactional
    public Trip resumeTrip(Integer tripId) {

        Trip trip = findById(tripId);

        requireStatus(trip, TripStatus.SUSPENDED);
        requireAssignedRequest(trip);

        validateAssignment(trip.getRequest(), trip.getDriver(), trip.getAuto());
        trip.setStatus(TripStatus.IN_PROGRESS);

        return trip;
    }

    private void validateAssignment(TransportRequest request, Driver driver, Auto auto) {

        if (auto.getAutoCondition() != AutoCondition.SERVICEABLE) {
            throw new IllegalStateException("Автомобиль должен быть исправен");
        }

        if (auto.getCargoType() != request.getCargoType()) {
            throw new IllegalStateException("Автомобиль не подходит для этого типа груза");
        }

        if (auto.getLoadCapacityKg() < request.getCargoWeightKg()) {
            throw new IllegalStateException("Грузоподъёмности автомобиля недостаточно");
        }

        int requiredExperience = request.getCargoType()
                .getRequiredExperienceYears();

        if (driver.getExperienceYears() < requiredExperience) {
            throw new IllegalStateException("Стажа водителя недостаточно для перевозки этого груза");
        }
    }

    private void requireStatus(Trip trip, TripStatus expectedStatus) {
        if (trip.getStatus() != expectedStatus) {
            throw new IllegalStateException("Для этой операции рейс должен иметь статус " + expectedStatus);
        }
    }

    private void requireAssignedRequest(Trip trip) {
        if (trip.getRequest().getRequestStatus()
                != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Заявка рейса должна иметь статус ASSIGNED");
        }
    }
}