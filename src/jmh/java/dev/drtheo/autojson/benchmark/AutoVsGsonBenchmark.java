package dev.drtheo.autojson.benchmark;

import com.google.gson.Gson;
import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.Bean;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3)
@Measurement(iterations = 10)
@Fork(value = 1, warmups = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class AutoVsGsonBenchmark {

    static final AutoJSON auto = new AutoJSON();
    static final JsonStringAdapter adapter = new JsonStringAdapter(auto);

    static final Gson gson = new Gson();
    static final Bean bean = new Bean();

    static final int iters = 1_000_000;

    static final String JSON = """
            {"primInt":2147483647,"primBool":true,"primByte":127,"primChar":"￿","primShort":32767,"primDouble":1.7976931348623157E308,"primFloat":3.4028235E38,"primLong":9223372036854775807, "intObj":2147483647,"boolObj":true,"byteObj":127,"charObj":"￿","shortObj":32767,"doubleObj":1.7976931348623157E308,"floatObj":3.4028235E38,"longObj":9223372036854775807,"hello":"HELLO MATE","id":{"namespace":"ait","path":"whatever"}}""";


}
