package dev.drtheo.autojson.adapter;

public interface JsonSerializationContext extends JsonContext {

    JsonObject object();
    JsonArray array();
    JsonPrimitive primitive(Object o);

    interface Built { }

    interface JsonObject {
        JsonObject put(String key, Object value);
        Built build();
    }

    interface JsonArray {
        JsonArray put(Object value);
        Built build();
    }

    interface JsonPrimitive extends Built { }
}
