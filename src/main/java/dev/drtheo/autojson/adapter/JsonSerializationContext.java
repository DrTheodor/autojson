package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public interface JsonSerializationContext extends JsonContext {

    Obj object();
    Array array();
    Primitive primitive();

    interface Built { }

    interface Obj {
        default Obj obj$put(String key, Object value) {
            return obj$put(key, value, null);
        }

        Obj obj$put(String key, Object value, Type type);
        Built obj$build();
    }

    interface Array {
        default Array array$element(Object value) {
            return array$element(value, null);
        }

        Array array$element(Object value, Type type);
        Built array$build();
    }

    interface Primitive {
        Array primitive$value(Object value);
        Built primitive$build();
    }
}
