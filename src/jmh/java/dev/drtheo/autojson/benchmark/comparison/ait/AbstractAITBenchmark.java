package dev.drtheo.autojson.benchmark.comparison.ait;

import com.google.gson.GsonBuilder;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.benchmark.comparison.AutoVsGsonBenchmark;
import dev.drtheo.autojson.benchmark.comparison.ait.data.*;
import dev.drtheo.autojson.schema.base.ObjectSchema;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AbstractAITBenchmark extends AutoVsGsonBenchmark {

    static Tardis tardis = new Tardis();
    static Path path = Path.of("src", "jmh", "resources", "ait.json");

    String raw = null;

    @Override
    public void setup() {
        super.setup();

        auto.schema(WorldKey.class, new WorldKeySchema());
        auto.schema(Id.class, new IdSchema());
        auto.schema(TardisHandlersManager.class, new HandlersManagerSchema());

        try {
            raw = Files.readString(Path.of("src/jmh/resources/ait.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AutoJSON createAuto() {
        return new AutoJSON() {

            @Override
            public boolean safeInstancing(Class<?> type) {
                if (TardisComponent.class.isAssignableFrom(type))
                    return true;

                return super.safeInstancing(type);
            }
        };
    }

    @Override
    public void setupGson(GsonBuilder builder) {
        builder.registerTypeAdapter(TardisHandlersManager.class, TardisHandlersManager.serializer());
        builder.registerTypeAdapter(Id.class, Id.serializer());
        builder.registerTypeAdapter(WorldKey.class, WorldKey.serializer());
    }

    public static void main(String[] args) throws IOException {
        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        auto.schema(WorldKey.class, new WorldKeySchema());
        auto.schema(Id.class, new IdSchema());
        auto.schema(TardisHandlersManager.class, new HandlersManagerSchema());

        for (int i = 0; i < 1_000_000; i++) {
            adapter.toJson(tardis);
        }

        Files.writeString(path, adapter.toJson(tardis));
        String raw = Files.readString(path);

        for (int i = 0; i < 1_000_000; i++) {
            adapter.fromJson(raw, Tardis.class);
        }
    }

    static class WorldKeySchema implements PrimitiveSchema<WorldKey> {

        @Override
        public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, WorldKey worldKey) {
            c.primitive$value(worldKey.registry.namespace + ":" + worldKey.registry.path
                    + "/" + worldKey.path.namespace + ":" + worldKey.path.path);
        }

        @Override
        public <To> WorldKey deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c) {
            String s = c.decodeBuiltIn();
            int slash = s.indexOf('/');

            String first = s.substring(0, slash);
            String second = s.substring(slash + 1);

            WorldKey key = new WorldKey(Id.fromString(second));
            key.registry = Id.fromString(first);

            return key;
        }
    }

    static class IdSchema implements PrimitiveSchema<Id> {

        @Override
        public <To> void serialize(JsonAdapter<Object, To> auto, JsonSerializationContext.Primitive c, Id id) {
            c.primitive$value(id.namespace + ":" + id.path);
        }

        @Override
        public <To> Id deserialize(JsonAdapter<Object, To> auto, JsonDeserializationContext c) {
            return Id.fromString(c.decodeBuiltIn());
        }
    }

    static class HandlersManagerSchema implements ObjectSchema<TardisHandlersManager> {

        @Override
        public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Obj obj, TardisHandlersManager manager) {
            manager.forEach(component -> {
                TardisComponent.IdLike idLike = component.getId();

                if (idLike == null) {
                    System.err.println("Id was null for " + component.getClass());
                    return;
                }

                obj.obj$put(idLike.name(), component);
            });
        }

        @Override
        public TardisHandlersManager instantiate() {
            return new TardisHandlersManager();
        }

        @Override
        public <To> void deserialize(JsonAdapter<Object, To> jsonAdapter, JsonDeserializationContext ctx, TardisHandlersManager manager, String s) {
            TardisComponentRegistry registry = TardisComponentRegistry.getInstance();
            TardisComponent.IdLike id = registry.get(s);

            if (id == null) {
                System.err.println("Can't find a subsystem id with name " + s);
                return;
            }

            try {
                manager.set(ctx.decodeCustom(id.clazz()));
            } catch (Throwable e) {
                System.out.println("Failed to deserialize subsystem " + id);
                e.printStackTrace(System.out);
            }
        }
    }
}
