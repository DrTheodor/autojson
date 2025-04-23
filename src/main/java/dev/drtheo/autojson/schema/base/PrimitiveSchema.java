package dev.drtheo.autojson.schema.base;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

/**
 * One of the base implementations of the {@link Schema} class.
 * Used for serializing into a primitive (number, character, boolean, string).
 *
 * @param <T> the type this schema serializes.
 */
public non-sealed interface PrimitiveSchema<T> extends Schema<T> {

    /**
     * @param adapter the adapter used to serialize this object.
     * @param ctx the serialization context. Used to provide the value.
     * @param t the object you're serializing.
     * @param <To> the type the adapter serializes to.
     */
    <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Primitive ctx, T t);

    @Override
    default byte type() {
        return SchemaType.PRIMITIVE;
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, T t) {
        JsonSerializationContext.Primitive prim = ctx.primitive();

        this.serialize(adapter, prim, t);

        return prim.primitive$build();
    }

    /**
     * @param adapter the adapter used to deserialize this object.
     * @param ctx the deserialization context. Used to query the deserialization.
     * @return The deserialized object.
     * @param <To> whatever the adapter's serializing to.
     */
    <To> T deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx);
}
