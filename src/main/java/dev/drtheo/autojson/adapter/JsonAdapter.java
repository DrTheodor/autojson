package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface JsonAdapter<From, To> extends SchemaHolder {

    default To toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }

    <T> To toJson(T obj, Type type);
    <R extends From> R fromJson(To object, Type type);

    default <R extends From> R fromJson(To object, Class<R> clazz) {
        return fromJson(object, (Type) clazz);
    }
}