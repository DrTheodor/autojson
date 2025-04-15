package dev.drtheo.autojson.schema.bake.unsafe.adapter;

import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;
import sun.misc.Unsafe;

public class ObjectClassAdapter implements ClassAdapter<Object, Object[]> {

    @Override
    public Object get(Unsafe unsafe, Object obj, long address) {
        return unsafe.getObject(obj, address);
    }

    @Override
    public void set(Unsafe unsafe, Object obj, long address, Object value) {
        unsafe.putObject(obj, address, value);
    }

    @Override
    public void set(Object[] ts, int index, Object value) {
        ts[index] = value;
    }
}
