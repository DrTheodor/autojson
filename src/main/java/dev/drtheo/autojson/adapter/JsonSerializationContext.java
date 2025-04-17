package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.PrimitiveSchema;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface JsonSerializationContext extends JsonContext {

    Obj object();
    Array array();
    Primitive primitive();

    interface Built { }

    interface Obj extends JsonContext {
        default Obj obj$put(String key, Object value) {
            return obj$put(key, value, value.getClass());
        }

        default Obj obj$put(String key, Object value, Type type) {
            return obj$put(key, value, type, schema(type));
        }

        <T> Obj obj$put(String key, T value, Type type, Schema<T> schema);

        Built obj$build();
    }

    interface Array extends JsonContext {
        default Array array$element(Object value) {
            return array$element(value, value.getClass());
        }

        default Array array$element(Object value, Type type) {
            return array$element(value, type, schema(type));
        }

        <T> Array array$element(T value, Type type, Schema<T> schema);
        Built array$build();
    }

    interface Primitive extends JsonContext {
        default void primitive$value(Object value) {
            primitive$value(value, value.getClass());
        }

        default <T> void primitive$value(T value, Type type) {
            Schema<T> schema = schema(type);

            if (schema != null && !(schema instanceof PrimitiveSchema<T>))
                throw new IllegalArgumentException("Schema " + schema + " is not a primitive schema");

            primitive$value(value, type, (PrimitiveSchema<T>) schema);
        }

        <T> void primitive$value(T value, Type type, PrimitiveSchema<T> schema);
        Built primitive$build();
    }
}
