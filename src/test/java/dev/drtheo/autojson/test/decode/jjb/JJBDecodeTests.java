package dev.drtheo.autojson.test.decode.jjb;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JJBDecodeTests {

    static String expected;
    static String raw;

    static final JsonStringAdapter adapter;

    static {
        AutoJSON auto = new AutoJSON();
        auto.setLossyNumbers(false);

        adapter = new JsonStringAdapter(auto);

        try {
            expected = Files.readString(Path.of("src/test/resources/big_expected.txt"));
            raw = Files.readString(Path.of("src/test/resources/big.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test() {
        Users result = adapter.fromJson(raw, Users.class);
        assertEquals(result.toString(), expected);
    }
}
