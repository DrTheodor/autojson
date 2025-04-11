package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.string.parser.JsonParseException;
import dev.drtheo.autojson.bake.unsafe.UnsafeUtil;

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
            return JsonStringDecoder.process(this, object, clazz);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Schema<T> schema(Class<T> t) {
        return auto.schema(t);
    }
}
