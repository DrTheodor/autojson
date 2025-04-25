package dev.drtheo.autojson.benchmark.comparison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.adapter.string.parser2.JsonStringAdapter2;
import dev.drtheo.autojson.benchmark.comparison.beans.Bean;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3)
@Measurement(iterations = 10)
@Fork(value = 1, warmups = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
public class AutoVsGsonBenchmark {

    protected static final int iters = 1;

    static final String JSON = """
            {"primInt":2147483647,"primBool":true,"primByte":127,"primChar":"e","primShort":32767,"primDouble":1.7976931348623157E308,"primFloat":3.4028235E38,"primLong":9223372036854775807, "intObj":2147483647,"boolObj":true,"byteObj":127,"charObj":"e","shortObj":32767,"doubleObj":1.7976931348623157E308,"floatObj":3.4028235E38,"longObj":9223372036854775807,"hello":"HELLO MATE","id":{"namespace":"ait","path":"whatever"}}""";

    protected AutoJSON auto;
    protected Gson gson;

    protected JsonStringAdapter adapter;
    Bean bean;

    @Setup(Level.Invocation)
    public void setup() {
        auto = createAuto();

        GsonBuilder builder = new GsonBuilder();
        setupGson(builder);
        gson = builder.create();

        adapter = new JsonStringAdapter2(auto);
        bean = new Bean();
    }

    public void setupGson(GsonBuilder builder) {

    }

    public AutoJSON createAuto() {
        return new AutoJSON();
    }
}
