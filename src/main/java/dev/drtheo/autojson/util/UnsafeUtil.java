package dev.drtheo.autojson.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {

    public static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == Boolean.class || clazz == Integer.class
                || clazz == Double.class || clazz == Float.class
                || clazz == Character.class || clazz == Byte.class
                || clazz == Short.class || clazz == Long.class;
    }

    public static boolean isChar(Class<?> clazz) {
        return clazz == char.class || clazz == Character.class;
    }

    public static boolean isNumber(Class<?> clazz) {
        return (clazz.isPrimitive() && (clazz == int.class || clazz == short.class
                || clazz == byte.class || clazz == double.class || clazz == float.class
                || clazz == long.class)) || Number.class.isAssignableFrom(clazz);
    }

    public static boolean isBool(Class<?> clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }

    public static int parseInt(String str) {
        if (str == null || str.isEmpty()) {
            throw new NumberFormatException("null or empty string");
        }

        final int len = str.length();
        int idx = 0;
        boolean negative = false;
        char firstChar = str.charAt(0);

        // Handle sign
        if (firstChar == '-') {
            negative = true;
            idx++;
        } else if (firstChar == '+') {
            idx++;
        }

        if (idx >= len) {
            throw new NumberFormatException(str);
        }

        int result = 0;
        while (idx < len) {
            char ch = str.charAt(idx++);
            if (ch < '0' || ch > '9') {
                throw new NumberFormatException(str);
            }

            // Check for overflow
            if (result > (Integer.MAX_VALUE - (ch - '0')) / 10) {
                throw new NumberFormatException("Overflow: " + str);
            }

            result = result * 10 + (ch - '0');
        }

        return negative ? -result : result;
    }

    public static double parseDouble(String str) {
        int idx = 0, end;
        boolean sign = false;
        char ch;
        double dval = 0.0;

        if (str == null || (end = str.length()) == 0 ||
                ((ch = str.charAt(0)) < '0' || ch > '9') &&
                        (!(sign = ch == '-') || ++idx == end ||
                                ((ch = str.charAt(idx)) < '0' || ch > '9')))
            throw new NumberFormatException(str);

        // Integer part
        for (; idx < end && (ch = str.charAt(idx)) >= '0' && ch <= '9'; idx++) {
            dval = dval * 10 + (ch - '0');
        }

        // Fraction part
        if (idx < end && ch == '.') {
            double fraction = 0.1;
            while (++idx < end && (ch = str.charAt(idx)) >= '0' && ch <= '9') {
                dval += (ch - '0') * fraction;
                fraction *= 0.1;
            }
        }

        // Exponent part
        if (idx < end && (ch == 'e' || ch == 'E')) {
            if (++idx == end) throw new NumberFormatException(str);

            boolean expSign = false;
            if ((ch = str.charAt(idx)) == '+' || ch == '-') {
                expSign = (ch == '-');
                if (++idx == end) throw new NumberFormatException(str);
            }

            int exp = 0;
            while (idx < end && (ch = str.charAt(idx)) >= '0' && ch <= '9') {
                exp = exp * 10 + (ch - '0');
                idx++;
            }

            if (expSign) exp = -exp;
            dval *= Math.pow(10, exp);
        }

        if (idx != end) throw new NumberFormatException(str);
        return sign ? -dval : dval;
    }

    public static long parseLong(String str) {
        if (str == null || str.isEmpty()) {
            throw new NumberFormatException("null or empty string");
        }

        final int len = str.length();
        int idx = 0;
        boolean negative = false;
        char firstChar = str.charAt(0);

        // Handle sign
        if (firstChar == '-') {
            negative = true;
            idx++;
        } else if (firstChar == '+') {
            idx++;
        }

        if (idx >= len) {
            throw new NumberFormatException(str);
        }

        long result = 0;
        while (idx < len) {
            char ch = str.charAt(idx++);
            if (ch < '0' || ch > '9') {
                throw new NumberFormatException(str);
            }

            // Check for overflow
            if (result > (Long.MAX_VALUE - (ch - '0')) / 10) {
                throw new NumberFormatException("Overflow: " + str);
            }

            result = result * 10 + (ch - '0');
        }

        return negative ? -result : result;
    }

    public static float parseFloat(String str) {
        return (float) parseDouble(str);
    }

    public static short parseShort(String str) {
        int val = parseInt(str);
        if (val < Short.MIN_VALUE || val > Short.MAX_VALUE) {
            throw new NumberFormatException("Value out of range for short: " + val);
        }
        return (short)val;
    }

    public static byte parseByte(String str) {
        int val = parseInt(str);
        if (val < Byte.MIN_VALUE || val > Byte.MAX_VALUE) {
            throw new NumberFormatException("Value out of range for byte: " + val);
        }
        return (byte)val;
    }
}
