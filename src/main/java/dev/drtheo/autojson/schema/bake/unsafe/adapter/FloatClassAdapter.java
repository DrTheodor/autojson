package dev.drtheo.autojson.schema.bake.unsafe.adapter;

import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class FloatClassAdapter extends ClassAdapter.Num<Float, float[]> {

    public FloatClassAdapter() {
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
    protected Float unwrap(Number number) {
        return number.floatValue();
    }
}
