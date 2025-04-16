package dev.drtheo.autojson;

import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface SchemaHolder {
    <R> Schema<R> schema(Type type);
}
