package dev.drtheo.autojson.schema.base;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

/**
 * One of the base implementations of the {@link Schema} class.
 * Used for serializing into an object format (key-value pairs).
 *
 * @param <T> the type this schema serializes.
 */
public non-sealed interface ObjectSchema<T> extends Schema<T> {

    /**
     * @param adapter the adapter used to serialize this object.
     * @param ctx the serialization context. Used to provide the key-value pairs.
     * @param t the object you're serializing.
     * @param <To> the type the adapter serializes to.
     */
    <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj ctx, T t);

    /**
     * @return A new instance of the object this schema serializes.
     */
    T instantiate();

    @Override
    default byte type() {
        return SchemaType.OBJECT;
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, T t) {
        JsonSerializationContext.Obj obj = ctx.object();

        this.serialize(adapter, obj, t);

        return obj.obj$build();
    }

    /**
     * @param adapter the adapter used to deserialize this object.
     * @param ctx the deserialization context. Used to query the deserialization.
     * @param t the object you're deserializing the data into.
     * @param field the name of the field you're deserializing.
     * @param <To> the type the adapter serializes to.
     */
    <To> void deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, T t, String field);
}
