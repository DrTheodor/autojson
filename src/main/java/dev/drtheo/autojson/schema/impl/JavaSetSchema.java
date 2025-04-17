package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

public class JavaSetSchema<T> implements ArraySchema<Set<T>, Set<T>> {

    private final Class<T> clazz;
    private final Schema<T> schema;

    public JavaSetSchema(SchemaHolder holder, ParameterizedType type) {
        this.clazz = (Class<T>) type.getActualTypeArguments()[0];
        this.schema = holder.schema(this.clazz);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, Set<T> objects) {
        for (T o : objects) {
            c.array$element(o, this.clazz, this.schema);
        }
    }

    @Override
    public Set<T> instantiate() {
        return new HashSet<>();
    }

    @Override
    public <To> Set<T> deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Set<T> set, int index) {
        set.add(c.decode(this.clazz, this.schema));
        return set;
    }
}
