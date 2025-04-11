package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.bake.unsafe.UnsafeUtil;

public class JsonStringBuilder implements JsonSerializationContext, JsonSerializationContext.JsonObject, JsonSerializationContext.JsonPrimitive, JsonSerializationContext.JsonArray {

    private final JsonStringAdapter adapter;

    private final StringBuilder builder = new StringBuilder();
    private boolean first = true;

    private int depth = -1;
    private boolean[] stack = new boolean[32];

    static final boolean IS_OBJECT = true;
    static final boolean IS_ARRAY = false;

    public JsonStringBuilder(JsonStringAdapter adapter) {
        this.adapter = adapter;
    }

    private void comma() {
        if (!first)
            builder.append(",");
    }

    @Override
    public JsonStringBuilder put(String key, Object value) {
        this.comma();

        if (key != null)
            builder.append("\"").append(key).append("\":");

        this.value(value);

        first = false;
        return this;
    }

    @Override
    public JsonArray put(Object value) {
        return this.put(null, value);
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

        this.builder.append(this.stack[depth--] == IS_OBJECT ? "}" : "]");
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public JsonObject object() {
        this.begin();

        this.builder.append("{");
        this.stack[depth] = IS_OBJECT;
        return this;
    }

    @Override
    public JsonArray array() {
        this.begin();

        this.builder.append('[');
        this.stack[depth] = IS_ARRAY;
        return this;
    }

    private void begin() {
        this.first = true;
        this.depth++;
    }

    @Override
    public JsonPrimitive primitive(Object o) {
        this.value(o);
        return this;
    }

    @Override
    public AutoJSON auto() {
        return this.adapter.auto;
    }
}
