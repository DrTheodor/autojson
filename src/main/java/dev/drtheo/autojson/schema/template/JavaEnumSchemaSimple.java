package dev.drtheo.autojson.schema.template;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;

public class JavaEnumSchemaSimple<T extends Enum<T>> implements PrimitiveSchema<T> {

    private final Class<T> enumClass;

    @SuppressWarnings("unchecked")
    public static <T, E extends Enum<E>> JavaEnumSchemaSimple<E> unwrap(Class<T> clazz) {
        return new JavaEnumSchemaSimple<>((Class<E>) clazz);
    }

    public JavaEnumSchemaSimple(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, T anEnum) {
        c.primitive$value(anEnum.toString());
    }

    @Override
    public <To> T deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c) {
        String s = c.decodeBuiltIn();

        if (s == null)
            return null;

        return Enum.valueOf(enumClass, s);
    }
}
