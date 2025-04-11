package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.bake.unsafe.UnsafeUtil;

public class JsonStringBuilder implements JsonSerializationContext, JsonSerializationContext.JsonObject, JsonSerializationContext.JsonPrimitive {

    private final JsonStringAdapter adapter;

    private final StringBuilder builder = new StringBuilder();
    private boolean first = true;

    public JsonStringBuilder(JsonStringAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public JsonStringBuilder put(String key, Object value) {
        if (!first)
            builder.append(",");

        builder.append("\"").append(key).append("\":");
        this.value(value);

        first = false;
        return this;
    }

    protected void value(Object value) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String || value instanceof Character) {
            //noinspection UnnecessaryToStringCall
            builder.append("\"").append(value.toString()).append("\"");
        } else if (UnsafeUtil.isPrimitive(value.getClass())) {
            //noinspection UnnecessaryToStringCall
            builder.append(value.toString());
        } else {
            this.adapter.toJson(this, value, value.getClass());
        }
    }

    @Override
    public JsonSerializationContext.Built build() {
        this.first = false;
        this.builder.append("}");
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public JsonObject object() {
        this.first = true;
        this.builder.append("{");
        return this;
    }

    @Override
    public JsonPrimitive primitive(Object o) {
        this.value(o);
        return this;
    }
}
