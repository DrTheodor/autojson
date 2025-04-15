package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.Schema;

import java.util.function.Supplier;

public interface JsonDeserializationContext extends JsonContext {
    <T> T decode(Class<T> clazz);
    <T> T decode(Class<T> clazz, Supplier<Schema<T>> schema);
}
