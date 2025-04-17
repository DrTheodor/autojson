package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface JsonContext {
    AutoJSON auto();

    <T> Schema<T> schema(Type type);
}
