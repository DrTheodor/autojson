package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.bake.UnsafeUtil;

import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {

    private final String value;

    public LazilyParsedNumber(String value) {
        this.value = value;
    }

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

    public long longValue() {
        try {
            return UnsafeUtil.parseLong(this.value);
        } catch (NumberFormatException var2) {
            return (new BigDecimal(this.value)).longValue();
        }
    }

    public float floatValue() {
        return UnsafeUtil.parseFloat(this.value);
    }

    public double doubleValue() {
        return UnsafeUtil.parseDouble(this.value);
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
}
