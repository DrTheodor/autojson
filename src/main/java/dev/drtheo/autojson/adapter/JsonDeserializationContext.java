package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.Schema;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public interface JsonDeserializationContext extends JsonContext {

    default <T> T decode(Type type) {
        return decode(type, null);
    }

    <T> T decode(Type type, Schema<T> schema);
}
