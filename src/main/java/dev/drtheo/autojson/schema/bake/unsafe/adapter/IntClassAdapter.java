package dev.drtheo.autojson.schema.bake.unsafe.adapter;

import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.util.function.Function;

public class IntClassAdapter extends ClassAdapter.Num<Integer, int[]> {

    public IntClassAdapter() {
        super(Integer.class, 0);
    }

    @Override
    public @NotNull Integer get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getInt(obj, address);
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
