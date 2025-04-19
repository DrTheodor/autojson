package dev.drtheo.autojson.schema.base;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

/**
 * One of the base implementations of the {@link Schema} class.
 * A schema used to serialize a class that wraps another object with its own schema.
 *
 * @param <T> the type this schema serializes.
 */
public non-sealed interface WrapperSchema<T> extends Schema<T> {

    @Override
    default ObjectSchema<T> asObject() {
        return this.child().asObject();
    }

    @Override
    default ArraySchema<T, ?> asArray() {
        return this.child().asArray();
    }

    @Override
    default PrimitiveSchema<T> asPrimitive() {
        return this.child().asPrimitive();
    }

    @Override
    default SchemaType type() {
        return child().type();
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, T t) {
        return child().serialize(adapter, ctx, t);
    }

    /**
     * @return the child schema this wrapper schema wraps around.
     */
    Schema<T> child();
}
