package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helpers for working with money as {@link BigDecimal} so that financial
 * values (balances, fares, refunds) never suffer from floating-point
 * rounding errors.
 */
public final class Money {

    private Money() {
    }

    /** Scale used for all monetary values (2 decimal places = cents). */
    private static final int SCALE = 2;

    /**
     * Wraps a double value into a {@link BigDecimal} using its decimal
     * string representation (no binary floating-point noise).
     *
     * @param value the double value
     * @return the equivalent BigDecimal
     */
    public static BigDecimal of(double value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Parses a money string (e.g. "10.50").
     *
     * @param value the string to parse
     * @return the parsed BigDecimal
     * @throws NumberFormatException if the value is not a valid number
     */
    public static BigDecimal parse(String value) {
        return new BigDecimal(value.trim());
    }

    /**
     * Returns the value rounded/scaled to exactly 2 decimal places.
     *
     * @param value the money value
     * @return the value scaled to 2 decimal places (half-up)
     */
    public static BigDecimal scale(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Formats a money value as a plain string with 2 decimal places
     * (e.g. "12.50") suitable for files and display.
     *
     * @param value the money value
     * @return the formatted string
     */
    public static String format(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP).toPlainString();
    }
}
