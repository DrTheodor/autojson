package dev.drtheo.autojson.benchmark;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.benchmark.ait.Id;
import dev.drtheo.autojson.benchmark.ait.Tardis;
import dev.drtheo.autojson.benchmark.ait.WorldKey;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class AITBenchmark extends AutoVsGsonBenchmark {

    static Tardis tardis = new Tardis();

    @Override
    public void setup() {
        super.setup();

        auto.schema(WorldKey.class, new WorldKeySchema());
        auto.schema(Id.class, new IdSchema());
    }

    @Benchmark
    public void autoSerialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            adapter.toJson(tardis);
        }
    }

//    @Benchmark
    public void gsonSerialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            gson.toJson(tardis);
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
}
