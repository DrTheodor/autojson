package dev.drtheo.autojson;

import dev.drtheo.autojson.schema.base.Schema;

import java.lang.reflect.Type;

public interface SchemaHolder {
    /**
     * @param type the type of the schema.
     * @return The schema for the specified type.
     * @param <R> the type that schema supports.
     */
    <R> Schema<R> schema(Type type);
}
