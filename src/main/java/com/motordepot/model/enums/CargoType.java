package com.motordepot.model.enums;

public enum CargoType {
    FOOD_LIQUID(2), // пищевой жидкий
    PETROLEUM(5),   // нефтяной
    BULK(1),        // сыпучка
    PACKAGED(0)     // упаковка
    ;
    private final int requiredExperienceYears;

    CargoType(int requiredExperienceYears) {
        this.requiredExperienceYears = requiredExperienceYears;
    }

    public int getRequiredExperienceYears() {
        return requiredExperienceYears;
    }
}
