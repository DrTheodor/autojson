package dev.drtheo.autojson.test.decode;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrimitiveDecodeTests {

    static final AutoJSON auto = new AutoJSON();
    static final JsonStringAdapter adapter = new JsonStringAdapter(auto);

    @Test
    public void intPrimitive() {
        int res = adapter.fromJson("1", int.class);
        assertEquals(1, res);
    }

    @Test
    public void boolPrimitive() {
        assertEquals(true, adapter.fromJson("true", boolean.class));
    }

    @Test
    public void floatPrimitive() {
        assertEquals(6.9f, adapter.fromJson("6.9", float.class));
    }

    @Test
    public void charPrimitive() {
        assertEquals('c', adapter.fromJson("\"c\"", char.class));
    }

    //

    @Test
    public void intObj() {
        Integer res = adapter.fromJson("1", Integer.class);
        assertEquals(1, res);
    }

    @Test
    public void boolObj() {
        Boolean res = adapter.fromJson("true", Boolean.class);
        assertEquals(true, res);
    }

    @Test
    public void floatObj() {
        Float res = adapter.fromJson("6.9", Float.class);
        assertEquals(6.9f, res);
    }

    @Test
    public void charObj() {
        Character res = adapter.fromJson("\"c\"", Character.class);
        assertEquals('c', res);
    }

    @Test
    public void str() {
        String res = adapter.fromJson("\"hello\"", String.class);
        assertEquals("hello", res);
    }

    @Test
    public void str2() {
        String res = adapter.fromJson("\"h\"", String.class);
        assertEquals("h", res);
    }
}
