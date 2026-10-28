package com.motordepot.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "motor_depots")
public class MotorDepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_depot_id")
    private List<Driver> drivers = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_depot_id")
    private List<Auto> autos = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_depot_id")
    private List<TransportRequest> transportRequests = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_depot_id")
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_depot_id")
    private List<RepairRequest> repairRequests = new ArrayList<>();

    protected MotorDepot() {
    }

    public MotorDepot(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public List<Auto> getAutos() {
        return autos;
    }

    public List<TransportRequest> getTransportRequests() {
        return transportRequests;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public List<RepairRequest> getRepairRequests() {
        return repairRequests;
    }

    @Override
    public String toString() {
        return "MotorDepot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}