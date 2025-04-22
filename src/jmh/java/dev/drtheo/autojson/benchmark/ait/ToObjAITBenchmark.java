package dev.drtheo.autojson.benchmark.ait;

import dev.drtheo.autojson.benchmark.ait.data.Tardis;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class ToObjAITBenchmark extends AbstractAITBenchmark {

    @Benchmark
    public void gsonDeserialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            gson.fromJson(raw, Tardis.class);
        }
    }

    @Benchmark
    public void autoDeserialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            adapter.fromJson(raw, Tardis.class);
        }
    }
}
