package dev.drtheo.autojson.benchmark.ait;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class ToJsonAITBenchmark extends AbstractAITBenchmark {


    @Benchmark
    public void autoSerialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            adapter.toJson(tardis);
        }
    }

    @Benchmark
    public void gsonSerialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            gson.toJson(tardis);
        }
    }
}
