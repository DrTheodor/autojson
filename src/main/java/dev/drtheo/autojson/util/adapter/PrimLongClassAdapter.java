package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class PrimLongClassAdapter extends ClassAdapter.Num<Long, long[]> {

    public PrimLongClassAdapter() {
        super(Long.class, 0L);
    }

    @Override
    public @NotNull Long get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getLong(obj, address);
    }

    @Override
    public int getLength(long[] ts) {
        return ts.length;
    }

    @Override
    public Long get(long[] ts, int index) {
        return ts[index];
    }

    @Override
    protected void set0(Unsafe unsafe, Object obj, long address, @NotNull Long value) {
        unsafe.putLong(obj, address, value);
    }

    @Override
    protected void set0(long[] ts, int index, @NotNull Long value) {
        ts[index] = value;
    }

    @Override
    public Long unwrap(Number number) {
        return number.longValue();
    }
}
