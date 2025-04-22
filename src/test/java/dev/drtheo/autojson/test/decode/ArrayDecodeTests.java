package dev.drtheo.autojson.test.decode;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayDecodeTests {

    static AutoJSON auto = new AutoJSON();
    static JsonStringAdapter adapter = new JsonStringAdapter(auto);

    @Test
    void intArray() {
        final String test = """
                [1, 2, 3]""";

        int[] a = adapter.fromJson(test, int[].class);
        assertArrayEquals(new int[] { 1, 2, 3 }, a);
    }

    @Test
    void stringArray() {
        final String test = """
                ["test", "test2", "test3"]""";

        String[] b = adapter.fromJson(test, String[].class);
        assertArrayEquals(new String[] { "test", "test2", "test3" }, b);
    }

    @Test
    void intArrayArray() {
        final String test = """
                [[1, 2, 3], [4, 5, 6]]""";

        int[][] c = adapter.fromJson(test, int[][].class);
        assertArrayEquals(new int[][] { { 1, 2, 3 }, { 4, 5, 6 } }, c);
    }

    @Test
    void objArray() {
        final String test = """
                [{"number":0,"name":"Theo"},{"number":1,"name":"Duzo"}]""";

        Bean[] d = adapter.fromJson(test, Bean[].class);
        Bean[] a = new Bean[2];
        a[0] = new Bean(0, "Theo");
        a[1] = new Bean(1, "Duzo");

        assertArrayEquals(a, d);
    }

    static class Bean {

        int number;
        String name;

        public Bean(int number, String name) {
            this.number = number;
            this.name = name;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Bean bean))
                return false;

            return number == bean.number && Objects.equals(name, bean.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, name);
        }

        @Override
        public String toString() {
            return "RootArrayTest{" +
                    "number=" + number +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
