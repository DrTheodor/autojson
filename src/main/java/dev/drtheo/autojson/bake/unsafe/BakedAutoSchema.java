package dev.drtheo.autojson.bake.unsafe;

import dev.drtheo.autojson.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BakedAutoSchema<T> implements Schema<T> {

    @SuppressWarnings("unchecked")
    public static <T> BakedAutoSchema<T> bake(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        FieldType<T, ?>[] types = new FieldType[fields.length];
        Map<String, FieldType<T, ?>> map = new HashMap<>(types.length);

        for (int i = 0; i < fields.length; i++) {
            FieldType<T, ?> type = FieldType.from(fields[i]);

            types[i] = type;
            map.put(type.name(), type);
        }

        return new BakedAutoSchema<>(clazz, types, map);
    }

    record FieldType<T, E>(Class<E> type, ClassAdapter<E> adapter, String name, long offset) {

        public static <T, E> FieldType<T, E> from(Field field) {
            Class<E> type = (Class<E>) field.getType();
            ClassAdapter<E> adapter = (ClassAdapter<E>) ClassAdapter.match(type);

            return new FieldType<>(type, adapter, field.getName(),
                    UnsafeUtil.UNSAFE.objectFieldOffset(field));
        }

        public E get(T obj) {
            return this.adapter.get(UnsafeUtil.UNSAFE, obj, offset);
        }

        public void set(T obj, E value) {
            this.adapter.set(UnsafeUtil.UNSAFE, obj, offset, value);
        }
    }

    private final Class<T> clazz;
    private final FieldType<T, ?>[] fields;
    private final Map<String, FieldType<T, ?>> map;

    private BakedAutoSchema(Class<T> clazz, FieldType<T, ?>[] fields, Map<String, FieldType<T, ?>> map) {
        this.clazz = clazz;
        this.fields = fields;
        this.map = map;
    }

    @Override
    public <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        if (!this.clazz.isInstance(t))
            throw new IllegalArgumentException(this.clazz + " != " + t.getClass());

        JsonSerializationContext.JsonObject object = c.object();

        for (FieldType<T, ?> type : this.fields) {
            object.put(type.name(), type.get(t));
        }

        return object.build();
    }

    @Override
    public T instantiate() {
        try {
            return (T) UnsafeUtil.UNSAFE.allocateInstance(this.clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, String field) {
        FieldType<T, ?> type = this.map.get(field);

        if (type == null) {
            if (c.auto().logMissingEntries())
                c.auto().log("Missing entry '" + field + "' on " + this.clazz);

            return;
        }

        deserialize(type, t, c);
    }

    private static <T, E> void deserialize(FieldType<T, E> field, T t, JsonDeserializationContext c) {
        field.set(t, c.decode(field.type));
    }
}
