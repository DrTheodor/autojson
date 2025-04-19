package dev.drtheo.autojson.schema.template;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.ArraySchema;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.util.ClassAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> the array type, e.g. {@code int[]}.
 * @param <E> the element type, e.g. {@link Integer} for {@code int}s.
 *
 * @implNote Uses reflections to create a new array instance and a
 * {@link ArrayList} to store the values temporary. Consider using
 * your own {@link ArraySchema} for your own
 */
public class JavaArraySchema<T, E> implements ArraySchema<T, List<E>> {

    private final Class<E> elementClass;
    private final ClassAdapter<E, ?> adapter;
    private final Schema<E> schema;

    @SuppressWarnings("unchecked")
    public static <T, E> JavaArraySchema<T, E> unwrap(SchemaHolder holder, Class<T> clazz) {
        return new JavaArraySchema<>(holder, (Class<E>) clazz.getComponentType());
    }

    @SuppressWarnings("unchecked")
    public JavaArraySchema(SchemaHolder holder, Class<E> elementClass) {
        this.elementClass = elementClass;
        this.adapter = (ClassAdapter<E, ?>) ClassAdapter.match(elementClass);
        this.schema = holder.schema(this.elementClass);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, T ts) {
        for (int i = 0; i < this.adapter.getArrayLength(ts); i++) {
            c.array$element(this.adapter.getArray(ts, i), this.elementClass, this.schema);
        }
    }

    @Override
    public List<E> instantiate() {
        return new ArrayList<>();
    }

    @Override
    public <To> List<E> deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, List<E> ts, int index) {
        ts.add(c.decode(elementClass, schema));
        return ts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T pack(List<E> l) {
        int size = l.size();

        Object array = Array.newInstance(elementClass, size);

        for (int i = 0; i < size; i++) {
            this.adapter.setArray(array, i, l.get(i));
        }

        return (T) array;
    }
}
