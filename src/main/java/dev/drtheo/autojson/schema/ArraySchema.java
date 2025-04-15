package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

public non-sealed interface ArraySchema<T> extends Schema<T> {

    <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, T t);

    /**
     * @return the array as an {@link Object}.
     */
    Object instantiate();

    default Object pack(Object obj) {
        return obj;
    }

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        JsonSerializationContext.Array obj = c.array();

        this.serialize(auto, obj, t);

        return obj.array$build();
    }

    <To> Object deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Object o, int index);
}
