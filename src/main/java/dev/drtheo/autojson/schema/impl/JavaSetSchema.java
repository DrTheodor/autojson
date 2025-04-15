package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class JavaSetSchema<T> implements ArraySchema<Set<T>> {

    private final Class<T> clazz;

    public JavaSetSchema(Type type) {
        if (!(type instanceof ParameterizedType parameterized))
            throw new IllegalArgumentException("Not a parameterized type");

        this.clazz = (Class<T>) parameterized.getActualTypeArguments()[0];
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
        return ((Set) o).add(c.decode(this.clazz));
    }
}
