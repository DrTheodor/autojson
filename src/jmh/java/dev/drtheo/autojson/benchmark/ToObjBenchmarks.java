package dev.drtheo.autojson.benchmark;

import dev.drtheo.autojson.Bean;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class ToObjBenchmarks extends AutoVsGsonBenchmark {

    @Benchmark
    public static void autoStr2Obj(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(adapter.fromJson(JSON, Bean.class));
        }
    }

    @Benchmark
    public static void gsonStr2Obj(Blackhole blackhole) {
        for (int i = 0; i < iters; i++) {
            blackhole.consume(adapter.fromJson(JSON, Bean.class));
        }
    }
}
