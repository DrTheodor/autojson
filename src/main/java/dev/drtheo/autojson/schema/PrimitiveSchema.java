package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;

public non-sealed interface PrimitiveSchema<T> extends Schema<T> {

    <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, T t);

    @Override
    default <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t) {
        JsonSerializationContext.Primitive prim = c.primitive();

        this.serialize(auto, prim, t);

        return prim.primitive$build();
    }

    <To> T deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c);
}
