package dev.drtheo.autojson.schema.base;

import dev.drtheo.autojson.adapter.JsonAdapter;

import java.lang.reflect.Type;

/**
 * One of the base implementations of the {@link Schema} class.
 * A schema used to serialize a class that wraps another object with its own schema.
 *
 * @param <T> the type this schema serializes.
 */
public non-sealed interface WrapperSchema<T, B> extends Schema<T> {

    @Override
    default <S> ObjectSchema<S> asObject() {
        return this.child().asObject();
    }

    @Override
    default <S> ArraySchema<S, ?> asArray() {
        return this.child().asArray();
    }

    @Override
    default <S> PrimitiveSchema<S> asPrimitive() {
        return this.child().asPrimitive();
    }

    @Override
    default SchemaType type() {
        return SchemaType.WRAPPER;
    }

    /**
     * @return the type you're wrapping (same as {@link T}).
     */
    Type wrapping();

    /**
     * @param adapter the adapter used to deserialize this object.
     * @param t the object you're deserializing the data into.
     * @return The deserialized wrapped object.
     * @param <To> the type the adapter serializes to.
     */
    <To> T deserialize(JsonAdapter<Object, To> adapter, B t);

    /**
     * @return the child schema this wrapper schema wraps around.
     */
    Schema<B> child();
}
