package dev.drtheo.autojson.schema.bake.unsafe.adapter;

import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class LongClassAdapter extends ClassAdapter.Num<Long, long[]> {

    public LongClassAdapter() {
        super(Long.class, 0L);
    }

    @Override
    public @NotNull Long get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getLong(obj, address);
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
    protected Long unwrap(Number number) {
        return number.longValue();
    }
}
