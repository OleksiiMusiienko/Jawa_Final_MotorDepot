package com.motordepot.service;

import com.motordepot.model.Driver;
import com.motordepot.model.enums.CargoType;
import com.motordepot.model.enums.DriverPaymentRate;
import com.motordepot.model.MotorDepot;
import com.motordepot.model.enums.TripStatus;
import com.motordepot.dao.DriverRepository;
import com.motordepot.dao.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final MotorDepotService motorDepotService;
    private final TripRepository tripRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository, MotorDepotService motorDepotService, TripRepository tripRepository) {
        this.driverRepository = driverRepository;
        this.motorDepotService = motorDepotService;
        this.tripRepository = tripRepository;
    }

    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    public Driver findById(Integer id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Водитель с ID " + id + " не найден"));
    }

    @Transactional
    public Driver create(Integer motorDepotId, String fullName, int experienceYears) {

        validateDriverData(fullName, experienceYears);

        MotorDepot depot = motorDepotService.getDepot();
        Driver driver = driverRepository.save(new Driver(fullName.trim(), experienceYears));
        depot.getDrivers().add(driver);

        return driver;
    }

    @Transactional
    public Driver update(Integer id, String fullName, int experienceYears) {

        validateDriverData(fullName, experienceYears);

        Driver driver = findById(id);
        driver.setFullName(fullName.trim());
        driver.setExperienceYears(experienceYears);

        return driver;
    }

    public Driver findAvailableDriver(CargoType cargoType) {
        if (cargoType == null) {
            throw new IllegalArgumentException("Необходимо указать тип груза");
        }

        int requiredExperience = cargoType.getRequiredExperienceYears();

        List<Driver> candidates = driverRepository.findAll().stream()
                .filter(driver -> driver.getExperienceYears() >= requiredExperience)
                .filter(driver -> !isBusy(driver.getId()))
                .toList();

        if (candidates.isEmpty()) {
            throw new IllegalStateException("Нет свободного водителя со стажем от " + requiredExperience + " лет");
        }

        int index = java.util.concurrent.ThreadLocalRandom.current().nextInt(candidates.size());
        return candidates.get(index);
    }

    public double calculatePayment(double transportationCost, int experienceYears) {

        if (!Double.isFinite(transportationCost)
                || transportationCost < 0) {
            throw new IllegalArgumentException("Стоимость перевозки должна быть конечным неотрицательным числом");
        }

        double percent = DriverPaymentRate.fromExperience(experienceYears).getPercent();
        double payment = transportationCost * (percent / 100.0);
        return Math.round(payment * 100.0) / 100.0;
    }


    private void validateDriverData(String fullName, int experienceYears) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Имя водителя не должно быть пустым");
        }

        if (fullName.trim().length() > 200) {
            throw new IllegalArgumentException("Имя водителя не должно превышать 200 символов");
        }

        if (experienceYears < 0) {
            throw new IllegalArgumentException("Стаж не может быть отрицательным");
        }
    }

    private boolean isBusy(Integer driverId) {
        return tripRepository.existsByDriver_IdAndStatusIn(
                driverId,
                List.of(
                        TripStatus.PLANNED,
                        TripStatus.IN_PROGRESS,
                        TripStatus.SUSPENDED
                )
        );
    }
}