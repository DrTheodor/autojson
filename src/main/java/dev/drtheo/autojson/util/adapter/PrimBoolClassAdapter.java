package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class PrimBoolClassAdapter extends ClassAdapter.Primitive<Boolean, boolean[]> {

    public PrimBoolClassAdapter() {
        super(false);
    }

    @Override
    public @NotNull Boolean get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getBoolean(obj, address);
    }

    @Override
    public int getLength(boolean[] ts) {
        return ts.length;
    }

    @Override
    public Boolean get(boolean[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Boolean value) {
        unsafe.putBoolean(obj, address, value);
    }

    @Override
    protected void set0(boolean[] ts, int index, @NotNull Boolean value) {
        ts[index] = value;
    }
}
