package dev.drtheo.autojson.schema;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.util.StringSchema;

import java.util.UUID;

public class UUIDSchema implements StringSchema<UUID> {

    @Override
    public <To> String serialize(JsonAdapter<Object, To> auto, UUID uuid) {
        return uuid.toString();
    }

    @Override
    public <To> UUID deserialize(JsonAdapter<Object, To> auto, String s) {
        if (s == null)
            return null;

        return UUID.fromString(s);
    }
}
