package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.util.UnsafeUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class JsonStringBuilder implements JsonSerializationContext, JsonSerializationContext.Obj, JsonSerializationContext.Primitive, JsonSerializationContext.Array, JsonSerializationContext.Built {

    private final JsonStringAdapter adapter;

    private final StringBuilder builder = new StringBuilder();
    private boolean first = true;

    public JsonStringBuilder(JsonStringAdapter adapter) {
        this.adapter = adapter;
    }

    private void begin() {
        this.first = true;
    }

    private void end() {
        this.first = false;
    }

    private <T> void put(String key, T value, Type type, @Nullable Schema<T> s) {
        if (!first)
            builder.append(",");

        if (key != null)
            builder.append("\"").append(key).append("\":");

        this.value(value, type, s);

        first = false;
    }

    protected <T> void value(T value, Type type, Schema<T> s) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof Character) {
            builder.append("\"").append((char) value).append("\"");
        } else if (value instanceof String str) {
            builder.append("\"").append(str).append("\"");
        } else if (UnsafeUtil.isPrimitive(value.getClass())) {
            //noinspection UnnecessaryToStringCall
            builder.append(value.toString());
        } else {
            this.adapter.toJson(this, value, type, s);
        }
    }

    @Override
    public <T> Array array$element(T value, Type type, Schema<T> s) {
        this.put(null, value, type, s);
        return this;
    }

    @Override
    public Built array$build() {
        this.builder.append("]");

        this.end();
        return this;
    }

    @Override
    public <T> Obj obj$put(String key, T value, Type type, Schema<T> s) {
        this.put(key, value, type, s);
        return this;
    }

    @Override
    public Built obj$build() {
        this.builder.append("}");

        this.end();
        return this;
    }

    @Override
    public <T> void primitive$value(T value, Type type, PrimitiveSchema<T> schema) {
        this.value(value, type, schema);
    }

    @Override
    public Built primitive$build() {
        this.end();
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public Obj object() {
        this.begin();

        this.builder.append("{");
        return this;
    }

    @Override
    public Array array() {
        this.begin();

        this.builder.append('[');
        return this;
    }

    @Override
    public Primitive primitive() {
        return this;
    }

    @Override
    public AutoJSON auto() {
        return this.adapter.auto;
    }

    @Override
    public <T> Schema<T> schema(Type type) {
        return adapter.schema(type);
    }
}
