package com.motordepot.model;

import com.motordepot.model.enums.AutoCondition;
import com.motordepot.model.enums.TripStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private TransportRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auto_id", nullable = false)
    private Auto auto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TripStatus status = TripStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "auto_condition_at_completion", length = 30)
    private AutoCondition autoConditionAtCompletion;

    @Column(name = "transportation_cost", nullable = false)
    private double transportationCost;

    @Column(name = "driver_payment", nullable = false)
    private double driverPayment;

    protected Trip() {
    }

    public Trip(TransportRequest request, Driver driver,
                Auto auto, double transportationCost) {
        this.request = request;
        this.driver = driver;
        this.auto = auto;
        this.transportationCost = transportationCost;
    }

    public Integer getId() {
        return id;
    }

    public TransportRequest getRequest() {
        return request;
    }

    public Driver getDriver() {
        return driver;
    }

    public Auto getAuto() {
        return auto;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public AutoCondition getAutoConditionAtCompletion() {
        return autoConditionAtCompletion;
    }

    public void setAutoConditionAtCompletion(
            AutoCondition autoConditionAtCompletion) {
        this.autoConditionAtCompletion = autoConditionAtCompletion;
    }

    public double getTransportationCost() {
        return transportationCost;
    }

    public void setTransportationCost(double transportationCost) {
        this.transportationCost = transportationCost;
    }

    public double getDriverPayment() {
        return driverPayment;
    }

    public void setDriverPayment(double driverPayment) {
        this.driverPayment = driverPayment;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", status=" + status +
                ", autoConditionAtCompletion=" + autoConditionAtCompletion +
                ", transportationCost=" + transportationCost +
                ", driverPayment=" + driverPayment +
                '}';
    }
}