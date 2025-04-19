package dev.drtheo.autojson.schema.impl;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.schema.impl.util.StringSchema;

import java.util.UUID;

public class UUIDSchema implements StringSchema<UUID> {

    @Override
    public <To> String serialize(JsonAdapter<Object, To> auto, UUID uuid) {
        return uuid.toString();
    }

    @Override
    public <To> UUID deserialize(JsonAdapter<Object, To> auto, String s) {
        return UUID.fromString(s);
    }
}
