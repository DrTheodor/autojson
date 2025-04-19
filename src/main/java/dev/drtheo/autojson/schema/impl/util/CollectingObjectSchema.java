package dev.drtheo.autojson.schema.impl.util;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ObjectSchema;

public class CollectingObjectSchema<T> implements ObjectSchema<T> {

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Obj c, T t) {

    }

    @Override
    public T instantiate() {
        return null;
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, T t, String field) {

    }
}
