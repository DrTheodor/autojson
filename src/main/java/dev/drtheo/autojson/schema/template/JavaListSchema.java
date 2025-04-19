package dev.drtheo.autojson.schema.template;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.ArraySchema;
import dev.drtheo.autojson.schema.base.Schema;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class JavaListSchema<T> implements ArraySchema.Simple<List<T>> {

    private final Class<T> type;
    private final Schema<T> schema;

    @SuppressWarnings("unchecked")
    public JavaListSchema(SchemaHolder holder, ParameterizedType type) {
        this.type = (Class<T>) type.getActualTypeArguments()[0];
        this.schema = holder.schema(this.type);
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Array c, List<T> ts) {
        for (T t : ts) {
            c.array$element(t, this.type, this.schema);
        }
    }

    @Override
    public List<T> instantiate() {
        return new ArrayList<>();
    }

    @Override
    public <To> List<T> deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c, List<T> o, int index) {
        o.add(index, c.decode(this.type, this.schema));
        return o;
    }
}
