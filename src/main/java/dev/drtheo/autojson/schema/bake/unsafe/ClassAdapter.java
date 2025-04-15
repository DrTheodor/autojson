package dev.drtheo.autojson.schema.bake.unsafe;

import dev.drtheo.autojson.schema.bake.unsafe.adapter.*;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public interface ClassAdapter<T, Array> {

    T get(Unsafe unsafe, Object obj, long address);
    void set(Unsafe unsafe, Object obj, long address, T value);

    default Array castArray(Object o) {
        return (Array) o;
    }

    default void setArray(Object obj, int index, T value) {
        set((Array) obj, index, value);
    }

    void set(Array ts, int index, T value);

    static ClassAdapter<?, ?> match(Class<?> clazz) {
        if (!clazz.isPrimitive())
            return OBJECT;

        if (clazz == Boolean.TYPE)
            return BOOL;

        if (clazz == Byte.TYPE)
            return BYTE;

        if (clazz == Short.TYPE)
            return SHORT;

        if (clazz == Character.TYPE)
            return CHAR;

        if (clazz == Integer.TYPE)
            return INT;

        if (clazz == Float.TYPE)
            return FLOAT;

        if (clazz == Double.TYPE)
            return DOUBLE;

        if (clazz == Long.TYPE)
            return LONG;

        return OBJECT;
    }

    abstract class Primitive<T, Array> implements ClassAdapter<T, Array> {
        private final T def;

        public Primitive(T def) {
            this.def = def;
        }

        @NotNull
        @Override
        public abstract T get(Unsafe unsafe, Object obj, long address);

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            if (value == null)
                value = def;

            this.set0(unsafe, obj, address, value);
        }

        @Override
        public void set(Array ts, int index, T value) {
            if (value == null)
                value = def;

            this.set0(ts, index, value);
        }

        protected abstract void set0(Unsafe unsafe, Object obj, long address, @NotNull T value);
        protected abstract void set0(Array ts, int index, @NotNull T value);
    }

    abstract class Wrapped<T, Array, B> extends Primitive<T, Array> {

        private final Class<T> clazz;
        protected final Class<B> base;

        public Wrapped(Class<T> clazz, Class<B> base, T def) {
            super(def);

            this.clazz = clazz;
            this.base = base;
        }

        protected abstract T unwrap(B b);

        @Override
        public void set(Unsafe unsafe, Object obj, long address, T value) {
            if (value.getClass() != clazz && base.isInstance(value))
                value = this.unwrap(base.cast(value));

            super.set(unsafe, obj, address, value);
        }

        @Override
        public void set(Array ts, int index, T value) {
            if (value.getClass() != clazz && base.isInstance(value))
                value = this.unwrap(base.cast(value));

            super.set(ts, index, value);
        }
    }

    abstract class Num<T extends Number, Array> extends Wrapped<T, Array, Number> {

        public Num(Class<T> clazz, T def) {
            super(clazz, Number.class, def);
        }
    }

    ClassAdapter<Boolean, boolean[]> BOOL = new BooleanClassAdapter();

    ClassAdapter<Character, char[]> CHAR = new CharClassAdapter();

    ClassAdapter<Byte, byte[]> BYTE = new ByteClassAdapter();

    ClassAdapter<Short, short[]> SHORT = new ShortClassAdapter();

    ClassAdapter<Integer, int[]> INT = new IntClassAdapter();

    ClassAdapter<Float, float[]> FLOAT = new FloatClassAdapter();

    ClassAdapter<Double, double[]> DOUBLE = new DoubleClassAdapter();

    ClassAdapter<Long, long[]> LONG = new LongClassAdapter();

    ClassAdapter<Object, Object[]> OBJECT = new ObjectClassAdapter();
}
