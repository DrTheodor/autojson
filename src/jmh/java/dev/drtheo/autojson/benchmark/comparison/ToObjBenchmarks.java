package dev.drtheo.autojson.benchmark.comparison;

import dev.drtheo.autojson.benchmark.comparison.beans.Bean;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class ToObjBenchmarks extends AutoVsGsonBenchmark {

    @Benchmark
    public void autoDeserialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(adapter.fromJson(JSON, Bean.class));
        }
    }

    @Benchmark
    public void gsonDeserialize(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(gson.fromJson(JSON, Bean.class));
        }
    }
}
