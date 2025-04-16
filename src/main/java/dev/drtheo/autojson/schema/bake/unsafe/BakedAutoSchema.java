package dev.drtheo.autojson.schema.bake.unsafe;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.annotation.Exclude;
import dev.drtheo.autojson.annotation.Instantiate;
import dev.drtheo.autojson.annotation.TypeHint;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BakedAutoSchema<T> implements ObjectSchema<T> {

    @SuppressWarnings("unchecked")
    public static <T> BakedAutoSchema<T> bake(AutoJSON auto, Class<T> clazz) {
        if (clazz.isAnonymousClass())
            return null;

        Instantiate options = clazz.getAnnotation(Instantiate.class);
        boolean safeInstance = false;

        if (options != null)
            safeInstance = options.safe();

        Field[] fields = clazz.getDeclaredFields();
        FieldType<T, ?>[] types = new FieldType[fields.length];

        // maybe use a linkedhashmap?
        Map<String, FieldType<T, ?>> map = new HashMap<>(types.length);

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (Modifier.isStatic(field.getModifiers()))
                continue;

            Exclude exclude = field.getAnnotation(Exclude.class);

            if (exclude != null && exclude.layer() != auto.layer())
                continue;

            FieldType<T, ?> type = FieldType.from(auto, field);

            types[i] = type;
            map.put(type.name(), type);
        }

        return new BakedAutoSchema<>(clazz, types, map, safeInstance);
    }

    record FieldType<T, E>(Type type, ClassAdapter<E, E[]> adapter, String name, long offset, Schema<E> schema) {

        public static <T, E> FieldType<T, E> from(AutoJSON auto, Field field) {
            Class<E> type = (Class<E>) field.getType();
            ClassAdapter<E, E[]> adapter = (ClassAdapter<E, E[]>) ClassAdapter.match(type);

            TypeHint hint = field.getAnnotation(TypeHint.class);
            Schema<E> schema = null;

            // FIXME @TypeHint
//            if (hint != null)
//                schema = new TypeWrapperSchema<>(
//                        auto, type, () -> Schema.createInstance(
//                                (Class<? extends E>) hint.value(), true)
//                );

            return new FieldType<>(field.getGenericType(), adapter,
                    field.getName(), UnsafeUtil.UNSAFE.objectFieldOffset(field), schema);
        }

        public E get(T obj) {
            return this.adapter.get(UnsafeUtil.UNSAFE, obj, offset);
        }

        public void set(T obj, E value) {
            this.adapter.set(UnsafeUtil.UNSAFE, obj, offset, value);
        }

        public Schema<E> schema(AutoJSON auto) {
            if (this.schema != null)
                return this.schema;

            return auto.schema(type);
        }
    }

    private final Class<T> clazz;
    private final FieldType<T, ?>[] fields;
    private final Map<String, FieldType<T, ?>> map;
    private final boolean safeInstance;

    private BakedAutoSchema(Class<T> clazz, FieldType<T, ?>[] fields, Map<String, FieldType<T, ?>> map, boolean safeInstance) {
        this.clazz = clazz;
        this.fields = fields;
        this.map = map;
        this.safeInstance = safeInstance;
    }

    @Override
    public String toString() {
        return "BakedAutoSchema{" +
                "clazz=" + clazz +
                ", fields=" + Arrays.toString(fields) +
                ", map=" + map +
                ", safeInstance=" + safeInstance +
                '}';
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Obj c, T t) {
        for (FieldType<T, ?> field : this.fields) {
            if (field == null)
                continue;

            c.obj$put(field.name(), field.get(t), field.type());
        }
    }

    @Override
    public T instantiate() {
        return Schema.createInstance(clazz, this.safeInstance);
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
        E e = c.decode(field.type(), field.schema(c.auto()));
        field.set(t, e);
    }
}
