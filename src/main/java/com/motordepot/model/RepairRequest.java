package com.motordepot.model;

import com.motordepot.model.enums.RepairStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "repair_requests")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auto_id", nullable = false)
    private Auto auto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RepairStatus status = RepairStatus.NEW;

    protected RepairRequest() {
    }

    public RepairRequest(Auto auto, Driver driver,
                         Trip trip, String description) {
        this.auto = auto;
        this.driver = driver;
        this.trip = trip;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public Auto getAuto() {
        return auto;
    }

    public Driver getDriver() {
        return driver;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RepairRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}