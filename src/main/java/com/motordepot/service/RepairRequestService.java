package com.motordepot.service;

import com.motordepot.model.*;
import com.motordepot.model.enums.AutoCondition;
import com.motordepot.model.enums.RepairStatus;
import com.motordepot.model.enums.TripStatus;
import com.motordepot.dao.AutoRepository;
import com.motordepot.dao.DriverRepository;
import com.motordepot.dao.RepairRequestRepository;
import com.motordepot.dao.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class RepairRequestService {

    private static final List<RepairStatus> OPEN_REPAIR_STATUSES = List.of(RepairStatus.NEW, RepairStatus.IN_PROGRESS);

    private static final List<TripStatus> ACTIVE_TRIP_STATUSES = List.of(TripStatus.PLANNED, TripStatus.IN_PROGRESS, TripStatus.SUSPENDED
    );

    private final RepairRequestRepository repairRequestRepository;
    private final AutoRepository autoRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final MotorDepotService motorDepotService;

    @Autowired
    public RepairRequestService(RepairRequestRepository repairRequestRepository, AutoRepository autoRepository, DriverRepository driverRepository,
            TripRepository tripRepository, MotorDepotService motorDepotService) {
        this.repairRequestRepository = repairRequestRepository;
        this.autoRepository = autoRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.motorDepotService = motorDepotService;
    }

    public List<RepairRequest> findAll() {
        return repairRequestRepository.findAll();
    }

    public RepairRequest findById(Integer id) {
        return repairRequestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Заявка на ремонт с ID " + id + " не найдена"));
    }

    @Transactional
    public RepairRequest createForTrip(Integer tripId, String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание неисправности не должно быть пустым");
        }

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NoSuchElementException("Рейс с ID " + tripId + " не найден"));

        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException("Сообщить о поломке в пути можно только для выполняющегося рейса");
        }

        RepairRequest repairRequest = createRequest(trip.getAuto(), trip.getDriver(), trip, description);
        trip.setStatus(TripStatus.SUSPENDED);
        return repairRequest;
    }

    @Transactional
    public RepairRequest createOutsideTrip(Integer autoId, Integer driverId, String description) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание неисправности не должно быть пустым");
        }

        Auto auto = autoRepository.findById(autoId).orElseThrow(() -> new NoSuchElementException("Автомобиль с ID " + autoId + " не найден"));
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new NoSuchElementException("Водитель с ID " + driverId + " не найден"));

        if (tripRepository.existsByAuto_IdAndStatusIn(autoId, ACTIVE_TRIP_STATUSES)) {
            throw new IllegalStateException("Автомобиль назначен на активный рейс.Для поломки в пути укажите рейс; "
                            + "запланированный рейс сначала отмените");
        }

        return createRequest(auto, driver, null, description);
    }

    @Transactional
    public RepairRequest startRepair(Integer repairRequestId) {
        RepairRequest request = findById(repairRequestId);

        if (request.getStatus() != RepairStatus.NEW) {
            throw new IllegalStateException("Начать ремонт можно только по новой заявке");
        }

        if (request.getAuto().getAutoCondition() != AutoCondition.NEEDS_REPAIR) {
            throw new IllegalStateException( "Автомобиль должен иметь состояние NEEDS_REPAIR");
        }

        request.setStatus(RepairStatus.IN_PROGRESS);
        request.getAuto().setAutoCondition(AutoCondition.UNDER_REPAIR);

        return request;
    }

    @Transactional
    public RepairRequest completeRepair(Integer repairRequestId) {
        RepairRequest request = findById(repairRequestId);

        if (request.getStatus() != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Завершить можно только начатый ремонт");
        }

        if (request.getAuto().getAutoCondition()
                != AutoCondition.UNDER_REPAIR) {
            throw new IllegalStateException("Автомобиль должен находиться в ремонте");
        }

        request.setStatus(RepairStatus.COMPLETED);
        request.getAuto().setAutoCondition(AutoCondition.SERVICEABLE);

        // Рейс остаётся SUSPENDED до явного возобновления
        return request;
    }

    private RepairRequest createRequest(Auto auto, Driver driver, Trip trip, String description) {

        if (repairRequestRepository.existsByAuto_IdAndStatusIn(auto.getId(), OPEN_REPAIR_STATUSES)) {
            throw new IllegalStateException("Заявка на ремонт уже создана");
        }

        if (auto.getAutoCondition() == AutoCondition.UNDER_REPAIR) {
            throw new IllegalStateException("Автомобиль в ремонте");
        }

        MotorDepot depot = motorDepotService.getDepot();
        RepairRequest request = repairRequestRepository.save(new RepairRequest(auto, driver, trip, description.trim()));

        auto.setAutoCondition(AutoCondition.NEEDS_REPAIR);
        depot.getRepairRequests().add(request);

        return request;
    }
}