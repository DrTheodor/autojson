package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

public non-sealed interface ObjectSchema<T> extends Schema<T> {

    <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Obj c, T t);

    T instantiate();

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        JsonSerializationContext.Obj obj = c.object();

        this.serialize(auto, obj, t);

        return obj.obj$build();
    }

    <To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, String field);
}
