package com.motordepot.model;

import com.motordepot.model.enums.CargoType;
import com.motordepot.model.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "transport_requests")
public class TransportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "destination", nullable = false, length = 200)
    private String destination;

    @Column(name = "cargo_weight_kg", nullable = false)
    private double cargoWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false, length = 30)
    private CargoType cargoType;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 30)
    private RequestStatus requestStatus = RequestStatus.NEW;

    protected TransportRequest() {
    }

    public TransportRequest(String destination, double cargoWeightKg,
                            CargoType cargoType) {
        this.destination = destination;
        this.cargoWeightKg = cargoWeightKg;
        this.cargoType = cargoType;
    }

    public Integer getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getCargoWeightKg() {
        return cargoWeightKg;
    }

    public void setCargoWeightKg(double cargoWeightKg) {
        this.cargoWeightKg = cargoWeightKg;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return "TransportRequest{" +
                "id=" + id +
                ", destination='" + destination + '\'' +
                ", cargoWeightKg=" + cargoWeightKg +
                ", cargoType=" + cargoType +
                ", requestStatus=" + requestStatus +
                '}';
    }
}