package dev.drtheo.autojson.schema.impl.util;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.PrimitiveSchema;

public interface StringSchema<T> extends PrimitiveSchema<T> {

    <To> String serialize(JsonAdapter<Object, To> auto, T t);

    <To> T deserialize(JsonAdapter<Object, To> auto, String s);

    @Override
    default <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, T t) {
        c.primitive$value(serialize(auto, t));
    }

    @Override
    default <To> T deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx) {
        return deserialize(adapter, ctx.<String>decode(String.class));
    }
}
