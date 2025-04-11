package dev.drtheo.autojson.bake.unsafe;

import sun.misc.Unsafe;

@FunctionalInterface
public interface UnsafeSetter<T> {
    void set(Unsafe unsafe, Object object, long address, T value);
}
