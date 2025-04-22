package dev.drtheo.autojson.test.decode;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectDecodeTests {

    static final AutoJSON auto = new AutoJSON();
    static final JsonStringAdapter adapter = new JsonStringAdapter(auto);

    @Test
    public void string() {
        assertEquals("test", adapter.fromJson("\"test\"", String.class));
    }
}
