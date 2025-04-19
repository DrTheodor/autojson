package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.util.UnsafeUtil;

/**
 * The base schema class.
 * </br>
 * You should probably implement {@link ObjectSchema}, {@link ArraySchema},
 * {@link PrimitiveSchema}, {@link WrapperSchema} or one of their derivatives instead.
 *
 * @param <T> the type this schema serializes.
 */
public sealed interface Schema<T> permits ArraySchema, ObjectSchema, PrimitiveSchema, WrapperSchema {

    /**
     * The type of the schema.
     * </br>
     * Instead of doing if-instanceof checks for the schema implementations,
     * the adapters should use the {@link Type} with a switch statement instead.
     *
     * @return the type of this schema.
     */
    Type type();

    <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, T t);

    // FIXME TypeHints
    //Schema<T> wrap(Class<? extends T> clazz);

    /**
     * Creates a new class of {@code T}.
     *
     * @param clazz the class you want to instantiate.
     * @param safe whether to use a safe or fast method.
     * @return A new instance of the specified class.
     * @param <T> the type of the class you want to instantiate.
     *
     * @implNote When using the safe method, it will create a new instance by
     * invoking the constructor via reflections. Otherwise, it will allocate
     * a new instance using {@link sun.misc.Unsafe}, which is faster, but
     * won't call the constructor, meaning that all fields will get their
     * default values ({@code 0} for numbers, {@code false} for
     * booleans and {@code null} for objects).
     */
    @SuppressWarnings("unchecked")
    static <T> T createInstance(Class<? extends T> clazz, boolean safe) {
        try {
            if (safe)
                return clazz.getConstructor().newInstance();

            return (T) UnsafeUtil.UNSAFE.allocateInstance(clazz);
        } catch (ReflectiveOperationException e) {
            System.err.println("Failed to create instance for class " + clazz);
            throw new RuntimeException(e);
        }
    }

    enum Type {
        PRIMITIVE,
        ARRAY,
        OBJECT,
        WRAPPER,
    }
}
