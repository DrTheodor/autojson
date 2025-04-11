package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.ast.JsonElement;
import dev.drtheo.autojson.ast.JsonObject;
import dev.drtheo.autojson.ast.JsonPrimitive;
import dev.drtheo.autojson.bake.unsafe.UnsafeUtil;

import java.io.IOException;

public class JsonStringAdapter implements JsonAdapter<Object, String> {

    protected final AutoJSON auto;

    public JsonStringAdapter(AutoJSON auto) {
        this.auto = auto;
    }

    @Override
    public <T> String toJson(T obj, Class<?> clazz) {
        if (UnsafeUtil.isPrimitive(clazz) || clazz == String.class)
            return obj.toString();

        JsonStringBuilder ctx = new JsonStringBuilder(this);

        toJson(ctx, obj, clazz);
        return ctx.toString();
    }

    protected <T> void toJson(JsonStringBuilder ctx, T obj, Class<?> clazz) {
        auto.schema(clazz).serialize(this, ctx, obj);
    }

    @Override
    public <R> R fromJson(String object, Class<R> clazz) {
        try {
            JsonElement element = JsonParser.parse(object);
            return fromJson(element, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> R fromJson(JsonElement json, Class<R> clazz) {
        if (json instanceof JsonObject o)
            return (R) this.auto.schema(clazz).deserialize(this, o);

        if (json instanceof JsonPrimitive p)
            return (R) p.unwrap();

        return null;
    }
}
