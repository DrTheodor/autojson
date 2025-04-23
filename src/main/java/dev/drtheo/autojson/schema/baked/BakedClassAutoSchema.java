package dev.drtheo.autojson.schema.baked;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.schema.base.ObjectSchema;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.annotation.Exclude;
import dev.drtheo.autojson.annotation.Instantiate;
import dev.drtheo.autojson.util.ClassAdapter;
import dev.drtheo.autojson.util.FastStringMap;
import dev.drtheo.autojson.util.Lazy;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class BakedClassAutoSchema<T> implements ObjectSchema<T> {

    @SuppressWarnings("unchecked")
    public static <T> BakedClassAutoSchema<T> bake(AutoJSON auto, Class<T> clazz) {
        if (clazz.isAnonymousClass())
            return null;

        Instantiate options = clazz.getAnnotation(Instantiate.class);
        boolean safeInstance = auto.safeInstancing(clazz);

        if (options != null)
            safeInstance = options.safe();

        List<Field> fields = new ArrayList<>();

        UnsafeUtil.getAllFields(fields, clazz, field -> {
            if (Modifier.isStatic(field.getModifiers()))
                return false;

            Exclude exclude = field.getAnnotation(Exclude.class);
            return exclude == null || !auto.shouldExclude(field, exclude.layer());
        });

        FieldType<T, ?>[] types = new FieldType[fields.size()];

        Map<String, FieldType<T, ?>> map = auto.useCustomFieldMap() ? new FastStringMap<>(types.length)
                : new HashMap<>(types.length);

        for (int i = 0; i < types.length; i++) {
            Field field = fields.get(i);
            FieldType<T, ?> type = FieldType.from(auto, field);

            types[i] = type;
            map.put(type.name(), type);
        }

        return new BakedClassAutoSchema<>(clazz, types, map, safeInstance);
    }

    public record FieldType<T, E>(Type type, ClassAdapter<E, E[]> adapter, String name, long offset, Lazy<Schema<E>> schema) {

        @SuppressWarnings("unchecked")
        public static <T, E> FieldType<T, E> from(SchemaHolder holder, Field field) {
            Type type = field.getGenericType();
            ClassAdapter<E, E[]> adapter = (ClassAdapter<E, E[]>) ClassAdapter.match(type);

            Lazy<Schema<E>> schema = new Lazy<>(() -> holder.schema(type));

            return new FieldType<>(type, adapter, field.getName(),
                    UnsafeUtil.UNSAFE.objectFieldOffset(field), schema);
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
    private final boolean safeInstance;

    private BakedClassAutoSchema(Class<T> clazz, FieldType<T, ?>[] fields, Map<String, FieldType<T, ?>> map, boolean safeInstance) {
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
            serialize(field, c, t);
        }
    }

    private static <T, E> void serialize(FieldType<T, E> field, JsonSerializationContext.Obj c, T t) {
        if (field == null)
            return;

        c.obj$put(field.name, field.get(t), field.type, field.schema.get());
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
                c.auto().warn("", "Missing entry '", field, "' on ", this.clazz);

            return;
        }

        deserialize(type, t, c);
    }

    private static <T, E> void deserialize(FieldType<T, E> field, T t, JsonDeserializationContext c) {
        E e = c.decode(field.type, field.schema.get());
        field.set(t, e);
    }
}
