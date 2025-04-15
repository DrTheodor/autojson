package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.string.parser.JsonParseException;
import dev.drtheo.autojson.util.UnsafeUtil;

import java.lang.reflect.Type;

public class JsonStringAdapter implements JsonAdapter<Object, String> {

    protected final AutoJSON auto;

    public JsonStringAdapter(AutoJSON auto) {
        this.auto = auto;
    }

    @Override
    public <T> String toJson(T obj, Class<?> clazz) {
        if (AutoJSON.isPrimitive(clazz))
            return obj.toString();

        JsonStringBuilder ctx = new JsonStringBuilder(this);

        toJson(ctx, obj, clazz);
        return ctx.toString();
    }

    protected <T> void toJson(JsonStringBuilder ctx, T obj, Class<?> clazz) {
        toJson(ctx, obj, clazz, null);
    }

    protected <T> void toJson(JsonStringBuilder ctx, T obj, Class<?> clazz, Type type) {
        Schema<T> s = auto.schema(clazz, type);
        try {
            if (s != null)
                s.serialize(this, ctx, obj);
        } catch (Exception e) {
            System.err.println("Failed to serialize " + s + "/" + clazz);
            throw e;
        }
    }

    @Override
    public <R> R fromJson(String object, Class<R> clazz) {
        try {
            return JsonStringParser.process(this, object, clazz);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Schema<T> schema(Class<T> t) {
        return auto.schema(t);
    }
}
