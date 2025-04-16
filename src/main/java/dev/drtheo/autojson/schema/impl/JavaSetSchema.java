package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

public class JavaSetSchema<T> implements ArraySchema<Set<T>> {

    private final Class<T> clazz;

    public JavaSetSchema(ParameterizedType type) {
        this.clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, Set<T> objects) {
        for (Object o : objects) {
            c.array$element(o);
        }
    }

    @Override
    public Object instantiate() {
        return new HashSet<>();
    }

    @Override
    public <To> Object deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Object o, int index) {
        ((Set<T>) o).add(c.decode(this.clazz));
        return o;
    }
}
