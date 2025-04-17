package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

public non-sealed interface ArraySchema<T, Intermediary> extends Schema<T> {

    <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, T t);

    /**
     * @return the array as an {@link Object}.
     */
    Intermediary instantiate();

    default T pack(Intermediary obj) {
        return (T) obj;
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        JsonSerializationContext.Array obj = c.array();

        this.serialize(auto, obj, t);

        return obj.array$build();
    }

    <To> Intermediary deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Intermediary o, int index);
}
