package dev.drtheo.autojson.adapter;

public interface JsonSerializationContext {

    JsonObject object();
    JsonPrimitive primitive(Object o);

    interface Built { }

    interface JsonObject {
        JsonObject put(String key, Object value);
        Built build();
    }

    interface JsonPrimitive extends Built { }
}
