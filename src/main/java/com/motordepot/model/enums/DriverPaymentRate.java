package com.motordepot.model.enums;

public enum DriverPaymentRate {
    BEGINNER(0, 10),
    EXPERIENCED(2, 15),
    SENIOR(5, 20),
    EXPERT(10, 25);

    private final int minExperienceYears;
    private final double percent;

    DriverPaymentRate(int minExperienceYears, double percent) {
        this.minExperienceYears = minExperienceYears;
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    public static DriverPaymentRate fromExperience(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Стаж не может быть отрицательным");
        }

        DriverPaymentRate result = BEGINNER;

        for (DriverPaymentRate rate : values()) {
            if (years >= rate.minExperienceYears && rate.minExperienceYears > result.minExperienceYears) {
                result = rate;
            }
        }

        return result;
    }
}
