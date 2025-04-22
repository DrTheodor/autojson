package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class PrimFloatClassAdapter extends ClassAdapter.Num<Float, float[]> {

    public PrimFloatClassAdapter() {
        super(Float.class, 0f);
    }

    @Override
    public @NotNull Float get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getFloat(obj, address);
    }

    @Override
    public int getLength(float[] ts) {
        return ts.length;
    }

    @Override
    public Float get(float[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Float value) {
        unsafe.putFloat(obj, address, value);
    }

    @Override
    protected void set0(float[] ts, int index, @NotNull Float value) {
        ts[index] = value;
    }

    @Override
    public Float unwrap(Number number) {
        return number.floatValue();
    }
}
