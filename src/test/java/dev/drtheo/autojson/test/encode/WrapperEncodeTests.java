package dev.drtheo.autojson.test.encode;

import dev.drtheo.autojson.test.AbstractWrapperTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WrapperEncodeTests extends AbstractWrapperTest {

    @Test
    void wrapper() {
        MyData data = new MyData();
        data.test = "hello";

        MyWrapper<MyData> wrapper1 = new MyWrapper<>();
        wrapper1.t = data;

        assertEquals("\"hello\"", adapter.toJson(wrapper1, wrapperType));
    }

    @Test
    void wrapperWrapper() {
        assertEquals(adapter.toJson(wrapperWrapper), "{\"wrapper\":\"hi\"}");
    }
}
