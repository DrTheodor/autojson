package dev.drtheo.autojson.benchmark;

import dev.drtheo.autojson.benchmark.beans.Bean;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

//@Warmup(iterations = 0)
//@Measurement(iterations = 0)
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
