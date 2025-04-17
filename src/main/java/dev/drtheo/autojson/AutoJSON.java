package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.schema.bake.unsafe.BakedAutoSchema;
import dev.drtheo.autojson.schema.impl.*;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class AutoJSON implements SchemaHolder {

    public static boolean isPrimitive(Type type) {
        return UnsafeUtil.isPrimitive(type) || type == String.class;
    }

    private final Map<Type, Schema<?>> schemas = new HashMap<>();
    private final Map<Type, TemplateCreator<?>> templates = new HashMap<>();

    private int layer = 0;
    private boolean logMisingEntries = true;

    public void setLogMisingEntries(boolean logMisingEntries) {
        this.logMisingEntries = logMisingEntries;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public <T> void template(Class<T> type, TemplateCreator<T> func) {
        templates.put(type, func);
    }

    public <T> Schema<T> schema(Type type, Schema<T> schema) {
        this.schemas.put(type, schema);
        return schema;
    }

    @Override
    public <T> Schema<T> schema(Type type) {
        if (type == null)
            return null;

        if (isPrimitive(type))
            return null;

        return (Schema<T>) UnsafeUtil.computeIfAbsent(schemas, type, this::createSchema);
    }

    @SuppressWarnings("unchecked")
    protected <T> Schema<T> createSchema(Type type) {
        if (type instanceof Class<?> clazz) {
            if (clazz.isArray())
                return (Schema<T>) JavaArraySchema.unwrap(this, clazz);

            if (clazz.isEnum())
                return (Schema<T>) JavaEnumSchema.unwrap(clazz);

            return (Schema<T>) BakedAutoSchema.bake(this, clazz);
        }

        if (type instanceof ParameterizedType parameterized) {
            if (parameterized.getRawType() == Set.class)
                return (Schema<T>) new JavaSetSchema<>(this, parameterized);

            if (parameterized.getRawType() == Map.class)
                return (Schema<T>) new JavaMapSchema<>(this, parameterized);

            if (parameterized.getRawType() == List.class)
                return (Schema<T>) new JavaListSchema<>(this, parameterized);

            TemplateCreator<?> creator = this.templates.get(parameterized.getRawType());

            if (creator != null)
                return (Schema<T>) creator.apply(this, parameterized);
        }

        throw new IllegalArgumentException("Can't handle type " + type);
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
        return logMisingEntries;
    }

    public void log(String message) {
        System.out.println("[INFO] " + message);
    }

    public void warn(String message) {
        System.err.println("[WARN] " + message);
    }

    @FunctionalInterface
    public interface TemplateCreator<T> extends BiFunction<SchemaHolder, ParameterizedType, Schema<T>> { }
}
