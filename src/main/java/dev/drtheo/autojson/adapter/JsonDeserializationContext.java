package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface JsonDeserializationContext extends JsonContext {

    <T> T decode(Type type);

    <T> T decode(Type type, Schema<T> schema);
}
