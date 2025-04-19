package dev.drtheo.autojson.adapter;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.schema.base.Schema;

import java.lang.reflect.Type;

/**
 * The context. Implemented by {@link JsonSerializationContext} and {@link JsonDeserializationContext}.
 */
public interface JsonContext extends SchemaHolder {
    /**
     * @return The {@link AutoJSON} instance used by the context.
     */
    AutoJSON auto();

    default SchemaHolder schemaHolder() {
        return auto();
    }

    @Override
    default <T> Schema<T> schema(Type type) {
        return schemaHolder().schema(type);
    }
}
