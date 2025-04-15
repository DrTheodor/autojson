package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.util.UnsafeUtil;

public sealed interface Schema<T> permits ArraySchema, ObjectSchema, PrimitiveSchema {
    <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> auto, JsonSerializationContext c, T t);

    // FIXME TypeHints
    //Schema<T> wrap(Class<? extends T> clazz);

    static <T> T createInstance(Class<? extends T> clazz, boolean safe) {
        try {
            if (safe)
                return clazz.getConstructor().newInstance();

            return (T) UnsafeUtil.UNSAFE.allocateInstance(clazz);
        } catch (ReflectiveOperationException e) {
            System.err.println("Failed to create instance for class " + clazz);
            throw new RuntimeException(e);
        }
    }
}
