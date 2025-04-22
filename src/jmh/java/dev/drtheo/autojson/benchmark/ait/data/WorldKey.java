package dev.drtheo.autojson.benchmark.ait.data;

import com.google.gson.*;

import java.lang.reflect.Type;

public class WorldKey {
    public Id path;
    public Id registry = new Id("minecraft", "world");

    public WorldKey(Id path) {
        this.path = path;
    }

    public static Object serializer() {
        return new Serializer();
    }

    static class Serializer implements JsonSerializer<WorldKey>, JsonDeserializer<WorldKey> {

        @Override
        public WorldKey deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsString();
            int slash = s.indexOf('/');

            String first = s.substring(0, slash);
            String second = s.substring(slash + 1);

            WorldKey key = new WorldKey(Id.fromString(second));
            key.registry = Id.fromString(first);

            return key;
        }

        @Override
        public JsonElement serialize(WorldKey src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new JsonPrimitive(src.registry.namespace + ":" + src.registry.path
                    + "/" + src.path.namespace + ":" + src.path.path);
        }
    }
}
