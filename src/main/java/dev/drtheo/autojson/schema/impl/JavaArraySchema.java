package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.bake.unsafe.ClassAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JavaArraySchema<T> implements ArraySchema<T[]> {

    private final Class<T> elementClass;
    private final ClassAdapter<T, ?> adapter;

    public static <T> JavaArraySchema<T> unwrap(Class<T> clazz) {
        return new JavaArraySchema<>((Class<T>) clazz.getComponentType());
    }

    public JavaArraySchema(Class<T> elementClass) {
        this.elementClass = elementClass;
        this.adapter = (ClassAdapter<T, ?>) ClassAdapter.match(elementClass);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, T[] ts) {
        for (T o : ts) {
            c.array$element(o);
        }
    }

    @Override
    public Object instantiate() {
        return new ArrayList<>();
    }

    @Override
    public <To> Object deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, Object ts, int index) {
        T d = c.decode(elementClass);

        ((List<T>) ts).add(d);
        return ts;
    }

    @Override
    public Object pack(Object obj) {
        List<?> l = (List<?>) obj;

        int size = l.size();

        Object array = Array.newInstance(elementClass, size);

        for (int i = 0; i < size; i++) {
            this.adapter.setArray(array, i, (T) l.get(i));
        }

        return array;
    }
}
