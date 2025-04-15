package dev.drtheo.autojson.test;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.Schema;

import java.util.Arrays;

public class RootArrayTest {

    int number;
    String name;

    @Override
    public String toString() {
        return "RootArrayTest{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) {
        final String TEST_STRING = """
                [1, 2, 3]""";

        final String TEST_STRING2 = """
                ["test", "test2", "test3"]""";

        final String TEST_STRING3 = """
                [[1, 2, 3], [4, 5, 6]]""";

        final String TEST_STRING4 = """
                [{"number":0,"name":"Theo"},{"number":1,"name":"Duzo"}]""";

        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        int[] a = adapter.fromJson(TEST_STRING, int[].class);
        System.out.println(Arrays.toString(a));

        System.out.println("---------------------------------");

        String[] b = adapter.fromJson(TEST_STRING2, String[].class);
        System.out.println(Arrays.toString(b));

        System.out.println("---------------------------------");

        int[][] c = adapter.fromJson(TEST_STRING3, int[][].class);

        for (int[] i : c) {
            System.out.println(Arrays.toString(i));
        }

        System.out.println("---------------------------------");

        RootArrayTest[] d = adapter.fromJson(TEST_STRING4, RootArrayTest[].class);

        System.out.println(Arrays.toString(d));
    }
}
