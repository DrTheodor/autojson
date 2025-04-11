package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

public interface Schema<T> {
    <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t);

    <To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, String field);

    T instantiate();
}
