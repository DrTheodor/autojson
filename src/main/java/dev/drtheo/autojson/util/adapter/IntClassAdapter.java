package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class IntClassAdapter extends ClassAdapter.Num<Integer, int[]> {

    public IntClassAdapter() {
        super(Integer.class, 0);
    }

    @Override
    public @NotNull Integer get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getInt(obj, address);
    }

    @Override
    public int getLength(int[] ts) {
        return ts.length;
    }

    @Override
    public Integer get(int[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Integer value) {
        unsafe.putInt(obj, address, value);
    }

    @Override
    protected void set0(int[] ts, int index, @NotNull Integer value) {
        ts[index] = value;
    }

    @Override
    protected Integer unwrap(Number number) {
        return number.intValue();
    }
}
