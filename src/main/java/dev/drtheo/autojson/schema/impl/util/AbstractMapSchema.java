package dev.drtheo.autojson.schema.impl.util;

import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.schema.impl.template.String2ObjectMapSchema;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * The base map schema.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 *
 * @implNote the key type should have a registered {@link StringSchema}.
 * Otherwise, you should write an adapter for it like {@link String2ObjectMapSchema}.
 */
public abstract class AbstractMapSchema<K, V> implements ObjectSchema<Map<K, V>> {

    private final Class<V> value;

    private final StringSchema<K> keySchema;
    private final Schema<V> valueSchema;

    @SuppressWarnings("unchecked")
    public AbstractMapSchema(SchemaHolder holder, ParameterizedType type) {
        Class<K> key = (Class<K>) type.getActualTypeArguments()[0];
        this.value = (Class<V>) type.getActualTypeArguments()[1];

        this.keySchema = this.findKeySchema(holder, key);
        this.valueSchema = holder.schema(value);
    }

    protected StringSchema<K> findKeySchema(SchemaHolder holder, Class<K> type) {
        Schema<K> keySchema = holder.schema(type);

        if (!(keySchema instanceof StringSchema<K> s))
            throw new IllegalArgumentException("Can't serialize a map with a non-string key! " +
                    "Consider implementing a string schema for " + type
                    + " or a map schema for Map<" + type.getSimpleName() + ", " + value.getSimpleName() + ">");

        return s;
    }

    @Override
    public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj ctx, Map<K, V> map) {
        map.forEach((s, t) -> ctx.obj$put(this.encodeKey(adapter, s), t, value, valueSchema));
    }

    @Override
    public Map<K, V> instantiate() {
        return new HashMap<>();
    }

    @Override
    public <To> void deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, Map<K, V> map, String key) {
        map.put(this.decodeKey(adapter, key), ctx.decode(value, valueSchema));
    }

    protected <To> K decodeKey(JsonAdapter<Object, To> adapter, String key) {
        return keySchema.deserialize(adapter, key);
    }

    protected <To> String encodeKey(JsonAdapter<Object, To> adapter, K key) {
        return keySchema.serialize(adapter, key);
    }
}