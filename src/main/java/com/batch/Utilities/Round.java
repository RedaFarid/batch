package com.batch.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Round {

    public Round() {
    }

    public static double RoundDouble(double num, int decimalPoints) {
        BigDecimal bd = BigDecimal.ZERO;
        try {
            bd = BigDecimal.valueOf(num);
            if (decimalPoints < 0) {
                decimalPoints = 1;
            }
            bd = bd.setScale(decimalPoints, RoundingMode.HALF_UP);
        } catch (Exception e) {
            bd = BigDecimal.ZERO;
        }
        return bd.doubleValue();
    }
}
