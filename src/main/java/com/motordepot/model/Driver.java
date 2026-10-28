package com.motordepot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    protected Driver(){
    }

    public Driver(String fullName, int experienceYears){
        this.fullName = fullName;
        this.experienceYears = experienceYears;
    }

    public Integer getId(){
        return id;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getExperienceYears() {
        return experienceYears;
    }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", experienceYears=" + experienceYears +
                '}';
    }
}
