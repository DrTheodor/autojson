package dev.drtheo.autojson.test.decode.jjb;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.adapter.string.parser2.JsonStringAdapter2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JJBTests {

    static String expected;
    static String raw;

    static final JsonStringAdapter adapter;

    static {
        AutoJSON auto = new AutoJSON();
        auto.setUseCustomFieldMap(true);
//        auto.setLossyNumbers(false);

        adapter = new JsonStringAdapter2(auto);

        try {
            expected = Files.readString(Path.of("src/test/resources/big_expected.txt"));
            raw = Files.readString(Path.of("src/test/resources/big.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deserialize() {
        for (int i = 0; i < 1_000_000; i++) {
            Users result = adapter.fromJson(raw, Users.class);
        }
    }

    @Test
    void serialize() {
        for (int i = 0; i < 1_000_000; i++) {
            adapter.toJson(new Users(), Users.class);
        }
    }
}
