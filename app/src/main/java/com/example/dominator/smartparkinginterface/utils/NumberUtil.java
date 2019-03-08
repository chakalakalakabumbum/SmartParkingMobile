
package com.example.dominator.smartparkinginterface.utils;

public class NumberUtil {

    public static int tryParseInt(String valueStr, int defaultValue) {
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float tryParseFloat(String valueStr, float defaultValue) {
        try {
            return Float.parseFloat(valueStr);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double tryParseDouble(String valueStr, double defaultValue) {
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
