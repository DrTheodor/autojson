package dev.drtheo.autojson.benchmark.ait.data;

import com.google.gson.*;

import java.lang.reflect.Type;

public class Id {
    public String namespace;
    public String path;

    public Id(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Id fromString(String s) {
        int colon = s.indexOf(':');
        String namespace = s.substring(0, colon);
        String path = s.substring(colon + 1);
        return new Id(namespace, path);
    }

    public static Object serializer() {
        return new Serializer();
    }

    static class Serializer implements JsonDeserializer<Id>, JsonSerializer<Id> {

        @Override
        public Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Id.fromString(json.getAsString());
        }

        @Override
        public JsonElement serialize(Id src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.namespace + ":" + src.path);
        }
    }
}
