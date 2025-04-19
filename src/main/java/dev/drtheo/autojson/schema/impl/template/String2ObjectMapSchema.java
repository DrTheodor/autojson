package dev.drtheo.autojson.schema.impl.template;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.impl.util.AbstractMapSchema;
import dev.drtheo.autojson.schema.impl.util.StringSchema;

import java.lang.reflect.ParameterizedType;

public class String2ObjectMapSchema<V> extends AbstractMapSchema<String, V> {

    public String2ObjectMapSchema(SchemaHolder holder, ParameterizedType type) {
        super(holder, type);
    }

    @Override
    protected StringSchema<String> findKeySchema(SchemaHolder holder, Class<String> type) {
        return null;
    }

    @Override
    protected <To> String encodeKey(JsonAdapter<Object, To> adapter, String key) {
        return key;
    }

    @Override
    protected <To> String decodeKey(JsonAdapter<Object, To> adapter, String key) {
        return key;
    }
}