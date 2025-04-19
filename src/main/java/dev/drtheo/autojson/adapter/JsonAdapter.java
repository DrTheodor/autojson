package dev.drtheo.autojson.adapter;

import java.lang.reflect.Type;

/**
 * The adapter class that allows to (de)serialize elements.
 *
 * @param <From> the type to serialize from or deserialize to (e.g. {@link Object}).
 * @param <To> the type to serialize to or deserialize from.
 */
public interface JsonAdapter<From, To> extends JsonContext {

    /**
     * @param obj the object to serialize.
     * @return the serialized object.
     */
    default To toJson(Object obj) {
        return toJson(obj, obj.getClass());
    }

    /**
     * @param obj the object to serialize.
     * @param type the type to serialize via.
     * @return the serialized object.
     * @param <T> the type of the object to serialize.
     */
    <T> To toJson(T obj, Type type);

    /**
     * @param object the object to deserialize.
     * @param clazz the type to deserialize via.
     * @return The deserialized object.
     * @param <R> the type to deserialize to.
     *
     * @implNote Equivalent to {@link #fromJson(Object, Type)}.
     */
    default <R extends From> R fromJson(To object, Class<R> clazz) {
        return fromJson(object, (Type) clazz);
    }

    /**
     * @param object the object to deserialize.
     * @param type the type to deserialize via.
     * @return The deserialized object.
     * @param <R> the type to deserialize to.
     */
    <R extends From> R fromJson(To object, Type type);
}