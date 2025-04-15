package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.schema.bake.unsafe.BakedAutoSchema;
import dev.drtheo.autojson.schema.impl.JavaArraySchema;
import dev.drtheo.autojson.schema.impl.JavaEnumSchema;
import dev.drtheo.autojson.schema.impl.JavaMapSchema;
import dev.drtheo.autojson.schema.impl.JavaSetSchema;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoJSON {

    public static boolean isPrimitive(Class<?> c) {
        return UnsafeUtil.isPrimitive(c) || c == String.class;
    }

    private final Map<Class<?>, Schema<?>> schemas = new HashMap<>();

    private int layer = 0;
    private boolean logMisingEntries = true;

    public void setLogMisingEntries(boolean logMisingEntries) {
        this.logMisingEntries = logMisingEntries;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public <T> Schema<T> schema(Class<?> clazz, Schema<T> schema) {
        this.schemas.put(clazz, schema);
        return schema;
    }

    @SuppressWarnings("unchecked")
    public <T> Schema<T> schema(Class<?> clazz) {
        return schema(clazz, (Type) null);
    }

    public <T> Schema<T> schema(Class<?> clazz, Type generic) {
        if (isPrimitive(clazz))
            return null;

        return (Schema<T>) schemas.computeIfAbsent(clazz,
                c -> createSchema(c, generic));
    }

    @SuppressWarnings("unchecked")
    protected <T> Schema<T> createSchema(Class<?> clazz, Type type) {
        if (clazz.isArray())
            return (Schema<T>) JavaArraySchema.unwrap(clazz);

        if (clazz.isEnum())
            return (Schema<T>) JavaEnumSchema.unwrap(clazz);

        if (Set.class.isAssignableFrom(clazz))
            return (Schema<T>) new JavaSetSchema<>(type);

        if (Map.class.isAssignableFrom(clazz))
            return (Schema<T>) new JavaMapSchema<>(type);

        return (Schema<T>) BakedAutoSchema.bake(this, clazz);
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj) {
        return toJson(adapter, obj, obj.getClass());
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj, Class<?> clazz) {
        return adapter.toJson(obj, clazz);
    }

    public <F, T> F fromJson(JsonAdapter<F, T> adapter, T object, Class<F> clazz) {
        return adapter.fromJson(object, clazz);
    }

    public int layer() {
        return layer;
    }

    public boolean logMissingEntries() {
        return true;
    }

    public void log(String message) {
        System.out.println("[INFO] " + message);
    }

    public void warn(String message) {
        System.err.println("[WARN] " + message);
    }
}
