package dev.drtheo.autojson.adapter.string.parser;

import dev.drtheo.autojson.util.UnsafeUtil;

import java.math.BigDecimal;

public class LazilyParsedNumber extends Number {

    protected final String value;

    public LazilyParsedNumber(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof LazilyParsedNumber other)) {
            return false;
        } else {
            return this.value.equals(other.value);
        }
    }

    @Override
    public int intValue() {
        return Integer.parseInt(this.value);
    }

    @Override
    public long longValue() {
        return Long.parseLong(this.value);
    }

    @Override
    public float floatValue() {
        return Float.parseFloat(this.value);
    }

    @Override
    public double doubleValue() {
        return Double.parseDouble(this.value);
    }

    public static class Lossy extends LazilyParsedNumber {

        public Lossy(String value) {
            super(value);
        }

        @Override
        public int intValue() {
            try {
                return UnsafeUtil.parseInt(this.value);
            } catch (NumberFormatException var4) {
                try {
                    return (int)Long.parseLong(this.value);
                } catch (NumberFormatException var3) {
                    return (new BigDecimal(this.value)).intValue();
                }
            }
        }

        @Override
        public long longValue() {
            try {
                return UnsafeUtil.parseLong(this.value);
            } catch (NumberFormatException var2) {
                return (new BigDecimal(this.value)).longValue();
            }
        }

        @Override
        public float floatValue() {
            return UnsafeUtil.parseFloat(this.value);
        }

        @Override
        public double doubleValue() {
            return UnsafeUtil.parseDouble(this.value);
        }

        @Override
        public short shortValue() {
            return UnsafeUtil.parseShort(this.value);
        }

        @Override
        public byte byteValue() {
            return UnsafeUtil.parseByte(this.value);
        }
    }
}
