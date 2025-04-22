package dev.drtheo.autojson.util;

import dev.drtheo.autojson.util.adapter.*;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Type;

public interface ClassAdapter<T, Array> {

    T get(Unsafe unsafe, Object obj, long address);
    void set(Unsafe unsafe, Object obj, long address, T value);

    @SuppressWarnings("unchecked")
    default Array castArray(Object o) {
        return (Array) o;
    }

    default void setArray(Object obj, int index, T value) {
        set(castArray(obj), index, value);
    }

    default int getArrayLength(Object obj) {
        return getLength(castArray(obj));
    }

    default T getArray(Object obj, int index) {
        return get(castArray(obj), index);
    }

    void set(Array ts, int index, T value);
    int getLength(Array ts);
    T get(Array ts, int index);

    static ClassAdapter<?, ?> match(Type type) {
        if (!(type instanceof Class<?> c) || !c.isPrimitive())
            return OBJECT;

        if (c == Boolean.TYPE)
            return BOOL;

        if (c == Byte.TYPE)
            return BYTE;

        if (c == Short.TYPE)
            return SHORT;

        if (c == Character.TYPE)
            return CHAR;

        if (c == Integer.TYPE)
            return INT;

        if (c == Float.TYPE)
            return FLOAT;

        if (c == Double.TYPE)
            return DOUBLE;

        if (c == Long.TYPE)
            return LONG;

        throw new IllegalArgumentException("Unsupported primitive type: " + type);
    }

    static ClassAdapter<?, ?> matchUnboxed(Type type) {
        if (!(type instanceof Class<?> c))
            return OBJECT;

        if (c == Boolean.TYPE || c == Boolean.class)
            return BOOL;

        if (c == Byte.TYPE || c == Byte.class)
            return BYTE;

        if (c == Short.TYPE || c == Short.class)
            return SHORT;

        if (c == Character.TYPE || c == Character.class)
            return CHAR;

        if (c == Integer.TYPE || c == Integer.class)
            return INT;

        if (c == Float.TYPE || c == Float.class)
            return FLOAT;

        if (c == Double.TYPE || c == Double.class)
            return DOUBLE;

        if (c == Long.TYPE || c == Long.class)
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

        public abstract T unwrap(B b);

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

    ClassAdapter<Boolean, boolean[]> BOOL = new PrimBoolClassAdapter();

    ClassAdapter<Character, char[]> CHAR = new PrimCharClassAdapter();

    ClassAdapter<Byte, byte[]> BYTE = new PrimByteClassAdapter();

    ClassAdapter<Short, short[]> SHORT = new PrimShortClassAdapter();

    ClassAdapter<Integer, int[]> INT = new PrimIntClassAdapter();

    ClassAdapter<Float, float[]> FLOAT = new PrimFloatClassAdapter();

    ClassAdapter<Double, double[]> DOUBLE = new PrimDoubleClassAdapter();

    ClassAdapter<Long, long[]> LONG = new PrimLongClassAdapter();

    ClassAdapter<Object, Object[]> OBJECT = new ObjectClassAdapter();
}
