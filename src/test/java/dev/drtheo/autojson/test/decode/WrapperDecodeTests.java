package dev.drtheo.autojson.test.decode;

import dev.drtheo.autojson.test.AbstractWrapperTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WrapperDecodeTests extends AbstractWrapperTest {

    @Test
    void data() {
        MyData data = new MyData();
        data.test = "hello";

        assertEquals(adapter.fromJson("\"hello\"", MyData.class), data);
    }

    @Test
    void wrapper() {
        assertEquals(adapter.fromJson("\"hi\"", wrapperType), wrapper);
    }

    @Test
    void wrapperWrapper() {
        MyData data1 = new MyData();
        data1.test = "hello";

        MyWrapper<MyData> wrapper1 = new MyWrapper<>();
        wrapper1.t = data1;

        MyWrapperWrapper wrapperWrapper1 = new MyWrapperWrapper();
        wrapperWrapper1.wrapper = wrapper1;

        assertEquals(adapter.fromJson("{\"wrapper\":\"hello\"}", MyWrapperWrapper.class), wrapperWrapper1);
    }
}
