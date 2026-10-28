package com.motordepot.service;

import com.motordepot.model.Driver;
import com.motordepot.model.Trip;
import com.motordepot.model.enums.TripStatus;
import com.motordepot.dao.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final TripRepository tripRepository;

    @Autowired
    public StatisticsService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
    private List<Trip> getCompletedTrips() {
        return tripRepository.findAllByStatus(TripStatus.COMPLETED);
    }

    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    // количество перевозок по каждому водиле
    public Map<Integer, Integer> getTripCountByDriver() {

        Map<Integer, Integer> result = new LinkedHashMap<>();

        for (Trip trip : getCompletedTrips()) {
            Integer driverId = trip.getDriver().getId();
            result.merge(driverId, 1, Integer::sum);
        }
        return result;
    }

    // Общая масса перевезённого груза каждым
    public Map<Integer, Double> getCargoWeightByDriver() {

        Map<Integer, Double> result = new LinkedHashMap<>();

        for (Trip trip : getCompletedTrips()) {
            Integer driverId = trip.getDriver().getId();
            double weight = trip.getRequest().getCargoWeightKg();
            result.merge(driverId, weight, Double::sum);
        }
        return result;
    }

    // Количество выполненных перевозок в каждый пункт назначения
    public Map<String, Integer> getTripCountByDestination() {

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Trip trip : getCompletedTrips()) {
            String destination = trip.getRequest().getDestination();
            result.merge(destination, 1, Integer::sum);
        }
        return result;
    }

    // Общая масса груза, доставленного в каждый пункт назначения
    public Map<String, Double> getCargoWeightByDestination() {

        Map<String, Double> result = new LinkedHashMap<>();
        for (Trip trip : getCompletedTrips()) {
            String destination = trip.getRequest().getDestination();
            double weight = trip.getRequest().getCargoWeightKg();
            result.merge(destination, weight, Double::sum);
        }

        return result;
    }

    // Общая сумма выплат каждому водиле.
    public Map<Integer, Double> getEarningsByDriver() {

        Map<Integer, Double> result = new LinkedHashMap<>();
        for (Trip trip : getCompletedTrips()) {
            Integer driverId = trip.getDriver().getId();
            result.merge(driverId, trip.getDriverPayment(), Double::sum);
        }
        result.replaceAll( (driverId, salary) -> roundMoney(salary) );

        return result;
    }

    // Водитель с наибольшей суммой выплат.
    public Optional<Driver> getTopEarningDriver() {

        List<Trip> trips = getCompletedTrips();
        Map<Integer, Double> salary = new LinkedHashMap<>();
        Map<Integer, Driver> drivers = new LinkedHashMap<>();

        for (Trip trip : trips) {
            Driver driver = trip.getDriver();
            Integer driverId = driver.getId();

            drivers.putIfAbsent(driverId, driver);
            salary.merge(driverId, trip.getDriverPayment(), Double::sum);
        }

        Integer topDriverId = null;
        double driverLider = -1;

        for (Map.Entry<Integer, Double> entry : salary.entrySet()) {
            double total = roundMoney(entry.getValue());
            if (topDriverId == null || total > driverLider || (Double.compare(total, driverLider) == 0 && entry.getKey() < topDriverId)) {
                topDriverId = entry.getKey();
                driverLider = total;
            }
        }
        if (topDriverId == null) {
            return Optional.empty();
        }
        return Optional.of(drivers.get(topDriverId));
    }

}