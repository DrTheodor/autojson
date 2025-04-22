package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class PrimShortClassAdapter extends ClassAdapter.Num<Short, short[]> {

    public PrimShortClassAdapter() {
        super(Short.class, (short) 0);
    }

    @Override
    public @NotNull Short get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getShort(obj, address);
    }

    @Override
    public int getLength(short[] ts) {
        return ts.length;
    }

    @Override
    public Short get(short[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Short value) {
        unsafe.putShort(obj, address, value);
    }

    @Override
    protected void set0(short[] ts, int index, @NotNull Short value) {
        ts[index] = value;
    }

    @Override
    public Short unwrap(Number number) {
        return number.shortValue();
    }
}
