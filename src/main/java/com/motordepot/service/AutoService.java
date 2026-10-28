package com.motordepot.service;

import com.motordepot.model.Auto;
import com.motordepot.model.enums.AutoCondition;
import com.motordepot.model.enums.CargoType;
import com.motordepot.model.MotorDepot;
import com.motordepot.model.enums.TripStatus;
import com.motordepot.dao.AutoRepository;
import com.motordepot.dao.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class AutoService {

    private final AutoRepository autoRepository;
    private final MotorDepotService motorDepotService;
    private final TripRepository tripRepository;

    @Autowired
    public AutoService(AutoRepository autoRepository, MotorDepotService motorDepotService, TripRepository tripRepository) {
        this.autoRepository = autoRepository;
        this.motorDepotService = motorDepotService;
        this.tripRepository = tripRepository;
    }

    public List<Auto> findAll() {
        return autoRepository.findAll();
    }

    public Auto findById(Integer id) {
        return autoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Автомобиль с ID " + id + " не найден" ));
    }

    @Transactional
    public Auto create(String registrationNumber, String brand, String model, double loadCapacityKg, CargoType cargoType) {

        validateAutoData(registrationNumber, brand, model, loadCapacityKg, cargoType);

        String normalizedNumber = normalizeNumber(registrationNumber);
        validateUniqueNumber(normalizedNumber, null);
        MotorDepot depot = motorDepotService.getDepot();
        Auto auto = autoRepository.save(new Auto(normalizedNumber, brand.trim(), model.trim(), loadCapacityKg, cargoType ));

        depot.getAutos().add(auto);
        return auto;
    }

    public Auto findAvailableAuto(CargoType cargoType, double cargoWeight){
        if (cargoType == null){
            throw  new IllegalArgumentException("Не указан тип груза");
        }

        if (cargoWeight <= 0) {
            throw new IllegalArgumentException("Вес груза должен быть больше нуля"
            );
        }
        //сортируем авто и фильтруем подходящий с наименьшей подхдящей грузоподьемностью
        List<Auto> autos = autoRepository.findAll().stream().filter(auto -> auto.getAutoCondition() == AutoCondition.SERVICEABLE)
                .filter(auto -> auto.getCargoType() == cargoType).filter(auto -> auto.getLoadCapacityKg() >= cargoWeight)
                .sorted(Comparator.comparingDouble(Auto::getLoadCapacityKg).thenComparing(Auto::getId)).toList();

        for(Auto auto: autos){
            if(!isBusy(auto.getId())){
                return auto;
            }
        }
        throw new IllegalStateException(
                "Нет свободного исправного автомобиля для груза " + cargoType + " массой " + cargoWeight + " кг"
        );
    }

    private boolean isBusy(Integer autoId) {
        return tripRepository.existsByAuto_IdAndStatusIn(
                autoId,
                List.of(
                        TripStatus.PLANNED,
                        TripStatus.IN_PROGRESS,
                        TripStatus.SUSPENDED
                )
        );
    }

    @Transactional
    public Auto update(Integer id, String registrationNumber, String brand, String model, double loadCapacityKg, CargoType cargoType) {

        validateAutoData(registrationNumber, brand, model, loadCapacityKg, cargoType);

        Auto auto = findById(id);

        if (isBusy(id)) {
            throw new IllegalStateException("Нельзя изменять автомобиль, назначенный на рейс");
        }

        String normalizedNumber = normalizeNumber(registrationNumber);
        validateUniqueNumber(normalizedNumber, id);

        auto.setRegistrationNumber(normalizedNumber);
        auto.setBrand(brand.trim());
        auto.setModel(model.trim());
        auto.setLoadCapacityKg(loadCapacityKg);
        auto.setCargoType(cargoType);

        return auto;
    }

    private void validateUniqueNumber(String registrationNumber, Integer currentAutoId) {

        autoRepository.findByRegistrationNumber(registrationNumber)
                .ifPresent(autos -> {
                    if (!autos.getId().equals(currentAutoId)) {
                        throw new IllegalArgumentException("Автомобиль с таким номером  уже существует");
                    }
                });
    }

    private String normalizeNumber(String registrationNumber) {
        if (registrationNumber == null) {
            throw new IllegalArgumentException("Регистрационный номер не должен быть пустым");
        }

        String normalizedNumber = registrationNumber.trim().toUpperCase(java.util.Locale.ROOT);

        if (!normalizedNumber.matches("[A-Z]{2}[0-9]{4}[A-Z]{2}")) {
            throw new IllegalArgumentException("Номер должен содержать две латинские буквы четыре цифры и две латинские буквы: AA1111AA"
            );
        }

        return normalizedNumber;
    }

    private void validateAutoData(String registrationNumber, String brand, String model, double loadCapacityKg, CargoType cargoType) {

        validateText(registrationNumber, "Регистрационный номер", 20);
        validateText(brand, "Марка", 100);
        validateText(model, "Модель", 100);

        if (!Double.isFinite(loadCapacityKg) || loadCapacityKg <= 0) {
            throw new IllegalArgumentException("Грузоподъёмность должна быть конечным числом больше нуля");
        }

        if (cargoType == null) {
            throw new IllegalArgumentException("Необходимо указать тип груза");
        }
    }

    private void validateText(String value, String text, int maxLength) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(text + ": значение не должно быть пустым");
        }

        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException(text + ": допускается не более " + maxLength + " символов");
        }
    }
}