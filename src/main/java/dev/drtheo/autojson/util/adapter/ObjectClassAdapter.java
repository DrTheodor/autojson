package dev.drtheo.autojson.util.adapter;

import dev.drtheo.autojson.util.ClassAdapter;
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

    @Override
    public int getLength(Object[] ts) {
        return ts.length;
    }

    @Override
    public Object get(Object[] ts, int index) {
        return ts[index];
    }
}
