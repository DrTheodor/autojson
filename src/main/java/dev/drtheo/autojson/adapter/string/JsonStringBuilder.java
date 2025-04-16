package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.util.UnsafeUtil;

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

    private void put(String key, Object value, Type type) {
        if (!first)
            builder.append(",");

        if (key != null)
            builder.append("\"").append(key).append("\":");

        this.value(value, type);

        first = false;
    }

    protected void value(Object value, Type type) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String || value instanceof Character) {
            //noinspection UnnecessaryToStringCall
            builder.append("\"").append(value.toString()).append("\"");
        } else if (UnsafeUtil.isPrimitive(value.getClass())) {
            //noinspection UnnecessaryToStringCall
            builder.append(value.toString());
        } else {
            this.adapter.toJson(this, value, type);
        }
    }

    @Override
    public Array array$element(Object value, Type type) {
        this.put(null, value, type);
        return this;
    }

    @Override
    public Built array$build() {
        this.builder.append("]");

        this.end();
        return this;
    }

    @Override
    public Obj obj$put(String key, Object value, Type type) {
        this.put(key, value, type);
        return this;
    }

    @Override
    public Built obj$build() {
        this.builder.append("}");

        this.end();
        return this;
    }

    @Override
    public Array primitive$value(Object value) {
        this.value(value, null);
        return this;
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
}
