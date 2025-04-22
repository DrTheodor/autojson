package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.string.parser.LazilyParsedNumber;
import dev.drtheo.autojson.logger.DelegateLogger;
import dev.drtheo.autojson.logger.Logger;
import dev.drtheo.autojson.logger.SystemLogger;
import dev.drtheo.autojson.schema.UUIDSchema;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.schema.baked.BakedClassAutoSchema;
import dev.drtheo.autojson.schema.template.*;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AutoJSON implements SchemaHolder, DelegateLogger {

    /**
     * @return {@code true} when the {@code type} is a primitive (boxed or not) or if it's a {@link String} or a {@link LazilyParsedNumber}.
     */
    public static boolean isBuiltIn(Type type) {
        return UnsafeUtil.isPrimitive(type) || type == String.class || type == LazilyParsedNumber.class;
    }

    private final Map<Type, Schema<?>> schemas = new HashMap<>();
    private final Map<Type, TemplateCreator<?>> templates = new HashMap<>();

    private final Logger logger;

    private int layer = 0;
    private boolean logMisingEntries = true;
    private boolean safeInstancing = false;

    public AutoJSON() {
        this.logger = this.setupLogger();

        this.defaultSchemas();
        this.defaultTemplates();
    }

    protected Logger setupLogger() {
        return new SystemLogger();
    }

    /**
     * @param logMisingEntries whether to log missing entries.
     * @see #logMissingEntries()
     */
    public void setLogMisingEntries(boolean logMisingEntries) {
        this.logMisingEntries = logMisingEntries;
    }

    /**
     * @return Whether to log missing object entries. Used by {@link BakedClassAutoSchema}.
     */
    public boolean logMissingEntries() {
        return logMisingEntries;
    }

    /**
     * @param safeInstancing whether to use safe instancing for all objects.
     * @see #safeInstancing(Class)
     */
    public void setSafeInstancing(boolean safeInstancing) {
        this.safeInstancing = safeInstancing;
    }

    /**
     * @return Whether to use safe instancing for the {@code type}.
     * @see Schema#createInstance(Class, boolean)
     */
    public boolean safeInstancing(Class<?> type) {
        return safeInstancing;
    }

    /**
     * @param layer the new layer(s) of this {@link AutoJSON} instance.
     * @see #getLayer()
     * @see #addLayer(int)
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * @param layer the layer(s) to add to this {@link AutoJSON} instance.
     * @see #getLayer()
     * @see #setLayer(int)
     */
    public void addLayer(int layer) {
        this.layer |= layer;
    }

    /**
     * @return the current layers of this {@link AutoJSON} instance.
     * @apiNote Read <a href="https://theo.is-a.dev/autojson/guides/layers/">here</a> for more info about layers.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Registers the default schemas (like {@link UUIDSchema} for {@link UUID}s and such).
     */
    protected void defaultSchemas() {
        this.schema(UUID.class, new UUIDSchema());
    }

    /**
     * Registers the default templates (like {@link JavaMapSchema} for {@link Map}s and such).
     */
    protected void defaultTemplates() {
        this.template(Map.class, JavaMapSchema::new);
    }

    /**
     * @return Whether to exclude the {@code layer}.
     * @apiNote Read <a href="https://theo.is-a.dev/autojson/guides/layers/">here</a> for more info about layers.
     */
    public boolean shouldExclude(Field field, int layer) {
        return layer < 1 || (this.layer & layer) != 0;
    }

    /**
     * Registers a new template. Used for generics, when the type erasure
     * is in action.
     *
     * @param type the base type.
     * @param func the lambda that creates a new schema based on the generic type.
     * @param <T> the type this template supports.
     *
     * @see #schema(Type, Schema)
     */
    public <T> void template(Class<? super T> type, TemplateCreator<T> func) {
        templates.put(type, func);
    }

    /**
     * Registers a new schema.
     *
     * @param type the base type.
     * @param schema the schema used for serialization of this type.
     * @param <T> the type this schema supports.
     *
     * @see #template(Class, TemplateCreator)
     */
    public <T> Schema<T> schema(Type type, Schema<T> schema) {
        this.schemas.put(type, schema);
        return schema;
    }

    /**
     * @param type the type for this schema. Can be a generic.
     * @return the schema used for serialization of this type.
     * Returns {@code null} if the {@code type} is a primitive or a built-in type.
     * @param <T> the type the schema should be able to deserialize.
     *
     * @see #isBuiltIn(Type)
     * @implNote By default, this method checks whether the type is a built-in, in
     * which case it returns {@code null}. Otherwise, it checks the cache and creates
     * a new schema if it's absent via {@link UnsafeUtil#computeIfAbsent(Map, Object, Function)}
     * and {@link #createSchema(Type)}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Schema<T> schema(Type type) {
        if (type == null)
            return null;

        if (isBuiltIn(type))
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

            return (Schema<T>) BakedClassAutoSchema.bake(this, clazz);
        }

        if (type instanceof ParameterizedType parameterized) {
            if (parameterized.getRawType() == Set.class || parameterized.getRawType() == HashSet.class)
                return (Schema<T>) new JavaSetSchema<>(this, parameterized);

            // since the templates map can't allow for one key having multiple values, this is a bit of a hack.
            if (parameterized.getRawType() == Map.class && parameterized.getActualTypeArguments()[0] == String.class)
                return (Schema<T>) new String2ObjectMapSchema<>(this, parameterized);

            if (parameterized.getRawType() == List.class || parameterized.getRawType() == ArrayList.class)
                return (Schema<T>) new JavaListSchema<>(this, parameterized);

            TemplateCreator<?> creator = this.templates.get(parameterized.getRawType());

            if (creator != null)
                return (Schema<T>) creator.apply(this, parameterized);
        }

        throw new IllegalArgumentException("Can't handle type " + type);
    }

    /**
     * @param adapter the adapter used to serialize the object.
     * @param obj the object to serialize.
     * @return the serialized object.
     * @param <F> the type the adapter deserializes from.
     * @param <T> the type the adapter serializes to.
     *
     * @implNote Equivalent to calling {@link #toJson(JsonAdapter, Object, Class)} with the {@code class} parameter being {@code obj.getClass()}.
     */
    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj) {
        return toJson(adapter, obj, obj.getClass());
    }

    /**
     * @param adapter the adapter used to serialize the object.
     * @param obj the object to serialize.
     * @param clazz the type to serialize via.
     * @return the serialized object.
     * @param <F> the type the adapter serializes from.
     * @param <T> the type the adapter serializes to.
     *
     * @implNote Equivalent to calling {@link JsonAdapter#toJson(Object, Type)}.
     */
    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj, Class<?> clazz) {
        return adapter.toJson(obj, clazz);
    }

    /**
     * @param adapter the adapter used to serialize the object.
     * @param obj the object to deserialize.
     * @param clazz the type to deserialize via.
     * @return the deserialized object.
     * @param <F> the type the adapter deserializes to.
     * @param <T> the type the adapter deserializes from.
     *
     * @implNote Equivalent to calling {@link JsonAdapter#fromJson(Object, Class)}.
     */
    public <F, T> F fromJson(JsonAdapter<F, T> adapter, T obj, Class<F> clazz) {
        return adapter.fromJson(obj, clazz);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @FunctionalInterface
    public interface TemplateCreator<T> extends BiFunction<SchemaHolder, ParameterizedType, Schema<T>> { }
}
