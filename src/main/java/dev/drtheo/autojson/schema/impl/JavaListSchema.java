package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class JavaListSchema<T> implements ArraySchema<List<T>> {

    private final Class<T> type;
    private final Schema<T> schema;

    public JavaListSchema(SchemaHolder holder, ParameterizedType type) {
        this.type = (Class<T>) type.getActualTypeArguments()[0];
        this.schema = holder.schema(this.type);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, List<T> ts) {
        for (T t : ts) {
            // FIXME(perf): we already have the schema cached
            //  so why bother querying it again in the impl?
            c.array$element(t, this.type);
        }
    }

    @Override
    public Object instantiate() {
        return new ArrayList<>();
    }

    @Override
    public <To> Object deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Object o, int index) {
        ((List<T>) o).add(index, c.decode(this.type, this.schema));
        return o;
    }
}
