package dev.drtheo.autojson.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

//@Warmup(iterations = 0)
//@Measurement(iterations = 0)
public class ToJsonBenchmarks extends AutoVsGsonBenchmark {

    @Benchmark
    public void gsonObj2Str(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(gson.toJson(bean));
        }
    }

    @Benchmark
    public void autoObj2Str(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(adapter.toJson(bean));
        }
    }
}
