package com.motordepot.config;

import com.motordepot.model.*;
import com.motordepot.model.enums.CargoType;
import com.motordepot.dao.AutoRepository;
import com.motordepot.dao.DriverRepository;
import com.motordepot.dao.MotorDepotRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("dev")
public class DataInitializer implements ApplicationRunner {

    private final MotorDepotRepository motorDepotRepository;
    private final DriverRepository driverRepository;
    private final AutoRepository autoRepository;

    public DataInitializer(
            MotorDepotRepository motorDepotRepository,
            DriverRepository driverRepository,
            AutoRepository autoRepository) {
        this.motorDepotRepository = motorDepotRepository;
        this.driverRepository = driverRepository;
        this.autoRepository = autoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (motorDepotRepository.count() > 0
                || driverRepository.count() > 0
                || autoRepository.count() > 0) {
            return;
        }

        MotorDepot depot = motorDepotRepository.save(
                new MotorDepot("Учебная автобаза")
        );

        List<Driver> drivers = driverRepository.saveAll(List.of(
                new Driver("Иван Петренко", 0),
                new Driver("Олег Иваненко", 2),
                new Driver("Андрей Коваленко", 5),
                new Driver("Сергей Бондаренко", 10)
        ));

        List<Auto> autos = autoRepository.saveAll(List.of(
                new Auto(
                        "AA1001BB", "MAN", "TGL",
                        5000, CargoType.PACKAGED
                ),
                new Auto(
                        "AA1002BB", "DAF", "XF",
                        15000, CargoType.PACKAGED
                ),
                new Auto(
                        "AA1003BB", "MAN", "TGS",
                        20000, CargoType.BULK
                ),
                new Auto(
                        "AA1004BB", "Volvo", "FM",
                        10000, CargoType.FOOD_LIQUID
                ),
                new Auto(
                        "AA1005BB", "Scania", "P",
                        18000, CargoType.PETROLEUM
                )
        ));

        depot.getDrivers().addAll(drivers);
        depot.getAutos().addAll(autos);
    }
}