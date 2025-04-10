package dev.drtheo.autojson.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3)
@Measurement(iterations = 10)
@Fork(value = 1, warmups = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class ToJsonBenchmarks extends AutoVsGsonBenchmark {

    @Benchmark
    public static void gsonObj2Str(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(gson.toJson(bean));
        }
    }

    @Benchmark
    public static void autoObj2Str(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(adapter.toJson(bean));
        }
    }
}
