package com.kaizen.gym_api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FitnessMetricsUtil {

    private FitnessMetricsUtil() {
    }

    public static double calculateEpley1RM(double weightKg, int reps) {
        if (weightKg <= 0 || reps <= 0) {
            return 0.0;
        }

        if (reps == 1) {
            return roundToTwoDecimalPlaces(weightKg);
        }

        double estimated1RM = weightKg * (1.0 + (reps / 30.0));
        return roundToTwoDecimalPlaces(estimated1RM);
    }

    private static double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
