package com.motordepot.model;

import com.motordepot.model.enums.AutoCondition;
import com.motordepot.model.enums.CargoType;
import jakarta.persistence.*;

@Entity
@Table(name = "autos")
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registration_number", nullable = false,
            unique = true, length = 20)
    private String registrationNumber;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "load_capacity_kg", nullable = false)
    private double loadCapacityKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false, length = 30)
    private CargoType cargoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "auto_condition", nullable = false, length = 30)
    private AutoCondition autoCondition = AutoCondition.SERVICEABLE;

    protected Auto() {
    }

    public Auto(String registrationNumber, String brand, String model,
                double loadCapacityKg, CargoType cargoType) {
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.loadCapacityKg = loadCapacityKg;
        this.cargoType = cargoType;
    }

    public Integer getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getLoadCapacityKg() {
        return loadCapacityKg;
    }

    public void setLoadCapacityKg(double loadCapacityKg) {
        this.loadCapacityKg = loadCapacityKg;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public AutoCondition getAutoCondition() {
        return autoCondition;
    }

    public void setAutoCondition(AutoCondition autoCondition) {
        this.autoCondition = autoCondition;
    }

    @Override
    public String toString() {
        return "Auto{" +
                "id=" + id +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", loadCapacityKg=" + loadCapacityKg +
                ", cargoType=" + cargoType +
                ", autoCondition=" + autoCondition +
                '}';
    }
}
