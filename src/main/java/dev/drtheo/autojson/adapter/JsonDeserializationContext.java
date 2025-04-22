package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;
import dev.drtheo.autojson.schema.base.Schema;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public interface JsonDeserializationContext extends JsonContext {

    /**
     * Decodes the value in the current context.
     *
     * @implNote Should only be used for built-in objects!
     *
     * @return The decoded built-in object.
     * @param <T> the type of the object.
     * @see dev.drtheo.autojson.AutoJSON#isBuiltIn(Type)
     */
    <T> T decodeBuiltIn();

    /**
     * Decodes the value in the current context.
     *
     * @implNote This method prefers the built-in values over
     * {@link PrimitiveSchema}s.
     * Consider using {@link #decode(Type, Schema)} if you want
     * to prioritize the schemas over built-in types.
     *
     * @param type the type of the object to decode.
     * @return The decoded object.
     * @param <T> the type of the object.
     */
    default <T> T decode(Type type) {
        if (AutoJSON.isBuiltIn(type))
            return decodeBuiltIn();

        return decodeCustom(type, schema(type));
    }

    /**
     * Decodes the value in the current context.
     *
     * @param type the type of the object to decode.
     * @return The decoded object.
     * @param <T> the type of the object.
     * @throws IllegalArgumentException when the type is non-built-in
     * and the provded schema is {@code null}.
     */
    default <T> T decode(Type type, Schema<T> schema) {
        if (schema == null) {
            if (AutoJSON.isBuiltIn(type))
                return decodeBuiltIn();

            throw new IllegalArgumentException("No schema for type " + type);
        }

        return decodeCustom(type, schema);
    }

    /**
     * Decodes a custom value in the current context.
     *
     * @implNote Should only be used for non-built-in objects!
     *
     * @param type the type of the object to decode.
     * @return The decoded object.
     * @param <T> the type of the object.
     * @throws NullPointerException if the schema is null.
     */
    default <T> T decodeCustom(Type type) {
        Schema<T> schema = schema(type);

        if (schema == null)
            throw new NullPointerException("Tried to use decodeCustom with no schema!");

        return decodeCustom(type, schema);
    }

    /**
     * Decodes the value in the current context.
     *
     * @implNote Should only be used for non-built-in objects!
     *
     * @param type the type of the object to decode.
     * @param schema the schema used to decode the value.
     * @return The decoded non-built-in object.
     * @param <T> the type of the object.
     */
    <T> T decodeCustom(Type type, @NotNull Schema<T> schema);
}
