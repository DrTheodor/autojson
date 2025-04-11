package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.ast.JsonElement;
import dev.drtheo.autojson.ast.JsonObject;

public interface Schema<T> {
    <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t);
    <To> T deserialize(JsonAdapter<Object, To> auto, JsonElement element);

    T instantiate();
    <To> void deserialize(JsonAdapter<Object, To> auto, T t, String field, JsonElement element);

}
