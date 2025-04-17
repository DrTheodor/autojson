package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

public interface JsonSerializationContext extends JsonContext {

    Obj object();
    Array array();
    Primitive primitive();

    interface Built { }

    interface Obj {
        default Obj obj$put(String key, Object value) {
            return obj$put(key, value, value.getClass());
        }

        Obj obj$put(String key, Object value, Type type);

        <T> Obj obj$put(String key, T value, Type type, Schema<T> schema);

        Built obj$build();
    }

    interface Array {
        default Array array$element(Object value) {
            return array$element(value, value.getClass());
        }

        Array array$element(Object value, Type type);

        <T> Array array$element(T value, Type type, Schema<T> schema);
        Built array$build();
    }

    interface Primitive {
        void primitive$value(Object value);
        Built primitive$build();
    }
}
