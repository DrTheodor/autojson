package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.schema.Schema;

import java.lang.reflect.Type;

/**
 *
 * @param <From>
 * @param <To>
 */
public interface JsonAdapter<From, To> extends SchemaHolder {

    default To toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }

    <T> To toJson(T obj, Type type);

    /**
     * @param object the object to deserialize.
     * @param clazz the type to deserialize via.
     * @return the deserialized object.
     * @param <R> the type to deserialize to.
     *
     * @implNote Equivalent to {@link #fromJson(Object, Type)}.
     */
    default <R extends From> R fromJson(To object, Class<R> clazz) {
        return fromJson(object, (Type) clazz);
    }

    /**
     * @param object
     * @param type
     * @return
     * @param <R>
     */
    <R extends From> R fromJson(To object, Type type);
}