package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class JavaMapSchema<T> implements ObjectSchema<Map<String, T>> {

    private final Class<T> value;
    private final Schema<T> schema;

    public JavaMapSchema(SchemaHolder holder, ParameterizedType type) {
        this.value = (Class<T>) type.getActualTypeArguments()[1];
        this.schema = holder.schema(value);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Obj c, Map<String, T> map) {
        map.forEach((s, t) -> c.obj$put(s, t, value, schema));
    }

    @Override
    public Map<String, T> instantiate() {
        return new HashMap<>();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Map<String, T> map, String field) {
        map.put(field, c.decode(value, schema));
    }
}