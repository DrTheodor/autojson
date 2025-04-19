package dev.drtheo.autojson.schema.base;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

/**
 * One of the base implementations of the {@link Schema} class.
 * Used for serializing into an array format (indexed values).
 *
 * @param <T> the type this schema serializes.
 * @param <Intermediary> the intermediary type used for collecting the elements.
 */
public non-sealed interface ArraySchema<T, Intermediary> extends Schema<T> {

    /**
     * @param adapter the adapter used to serialize this object.
     * @param ctx the serialization context. Used to provide the values
     *            in ascending order (from {@code arr[0]} to {@code arr[N]}).
     * @param t the object you're serializing.
     * @param <To> the type the adapter serializes to.
     */
    <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Array ctx, T t);

    /**
     * @return a new instance of the {@link Intermediary} type.
     */
    Intermediary instantiate();

    /**
     * Converts the {@link Intermediary} type into the built {@link T} type.
     *
     * @param obj the {@link Intermediary} object.
     * @return the built {@link T} object.
     */
    T pack(Intermediary obj);

    @Override
    default SchemaType type() {
        return SchemaType.ARRAY;
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, T t) {
        JsonSerializationContext.Array obj = ctx.array();

        this.serialize(adapter, obj, t);

        return obj.array$build();
    }

    /**
     * @param adapter the adapter used to deserialize this object.
     * @param ctx the deserialization context. Used to query the deserialization.
     * @param o the object you're deserializing the data into.
     * @param index the index of the element, maintaining the order used
     *              for serialization in {@link #serialize(JsonAdapter, JsonSerializationContext.Array, Object)}.
     * @return the new {@link Intermediary} value.
     * @param <To> the type the adapter serializes to.
     */
    <To> Intermediary deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, Intermediary o, int index);

    /**
     * A utility class that implements {@link ArraySchema} without
     * an intermediary value.
     * @param <T> the object type this schema serializes.
     */
    interface Simple<T> extends ArraySchema<T, T> {

        @Override
        default T pack(T obj) {
            return obj;
        }
    }
}
