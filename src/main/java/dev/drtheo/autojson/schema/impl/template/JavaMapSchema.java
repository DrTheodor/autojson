package dev.drtheo.autojson.schema.impl.template;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.schema.impl.util.AbstractMapSchema;

import java.lang.reflect.ParameterizedType;

/**
 * The default implementation of {@link AbstractMapSchema}.
 * Consider extending it instead of this class.
 */
public class JavaMapSchema<K, V> extends AbstractMapSchema<K, V> {

    public JavaMapSchema(SchemaHolder holder, ParameterizedType type) {
        super(holder, type);
    }
}
