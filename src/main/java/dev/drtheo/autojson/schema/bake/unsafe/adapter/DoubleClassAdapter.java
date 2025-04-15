package dev.drtheo.autojson.schema.bake.unsafe.adapter;

import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class DoubleClassAdapter extends ClassAdapter.Num<Double, double[]> {

    public DoubleClassAdapter() {
        super(Double.class, 0d);
    }

    @Override
    public @NotNull Double get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getDouble(obj, address);
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Double value) {
        unsafe.putDouble(obj, address, value);
    }

    @Override
    protected void set0(double[] ts, int index, @NotNull Double value) {
        ts[index] = value;
    }

    @Override
    protected Double unwrap(Number number) {
        return number.doubleValue();
    }
}
