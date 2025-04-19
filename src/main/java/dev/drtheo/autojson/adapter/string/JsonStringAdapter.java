package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.string.parser.JsonParseException;

import java.lang.reflect.Type;

public class JsonStringAdapter implements JsonAdapter<Object, String> {

    protected final AutoJSON auto;

    public JsonStringAdapter(AutoJSON auto) {
        this.auto = auto;
    }

    @Override
    public <T> String toJson(T obj, Type type) {
        if (AutoJSON.isBuiltIn(type))
            return obj.toString();

        JsonStringBuilder ctx = new JsonStringBuilder(this);

        toJson(ctx, obj, type, auto.schema(type));
        return ctx.toString();
    }

    protected <T> void toJson(JsonStringBuilder ctx, T obj, Type type, Schema<T> s) {
        try {
            s.serialize(this, ctx, obj);
        } catch (Exception e) {
            System.err.println("Failed to serialize " + type);
            throw e;
        }
    }

    @Override
    public <R> R fromJson(String object, Type clazz) {
        try {
            return JsonStringParser.process(this, object, clazz);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AutoJSON auto() {
        return auto;
    }
}
