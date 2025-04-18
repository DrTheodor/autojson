package dev.drtheo.autojson.schema.template;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;

public class JavaEnumSchema<T extends Enum<T>> implements PrimitiveSchema<T> {

    private final Class<T> enumClass;

    @SuppressWarnings("unchecked")
    public static <T, E extends Enum<E>> JavaEnumSchema<E> unwrap(Class<T> clazz) {
        return new JavaEnumSchema<>((Class<E>) clazz);
    }

    public JavaEnumSchema(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, T anEnum) {
        c.primitive$value(anEnum.toString());
    }

    @Override
    public <To> T deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c) {
        return Enum.valueOf(enumClass, c.decodeBuiltIn());
    }
}
