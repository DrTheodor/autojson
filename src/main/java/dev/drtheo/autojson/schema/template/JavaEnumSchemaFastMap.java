package dev.drtheo.autojson.schema.template;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;
import dev.drtheo.autojson.util.FastStringMap;

public class JavaEnumSchemaFastMap<T extends Enum<T>> implements PrimitiveSchema<T> {

    private final FastStringMap<T> map;

    @SuppressWarnings("unchecked")
    public static <T, E extends Enum<E>> JavaEnumSchemaFastMap<E> unwrap(Class<T> clazz) {
        return new JavaEnumSchemaFastMap<>((Class<E>) clazz);
    }

    public JavaEnumSchemaFastMap(Class<T> enumClass) {
        T[] consts = enumClass.getEnumConstants();
        this.map = new FastStringMap<>(consts.length);

        for (T t : consts) {
            map.put(t.toString(), t);
        }
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

        return this.map.get(s);
    }
}
