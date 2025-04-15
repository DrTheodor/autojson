package dev.drtheo.autojson.test;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;

public class RootPrimitiveTest {

    public static void main(String[] args) {
        String TEST1 = "1";
        String TEST2 = "false";
        String TEST3 = "1.0";
        String TEST4 = "\"4\"";
        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        System.out.println(adapter.fromJson(TEST1, int.class));
        System.out.println(adapter.fromJson(TEST2, boolean.class));
        System.out.println(adapter.fromJson(TEST3, float.class));
        System.out.println(adapter.fromJson(TEST4, String.class));
        System.out.println(adapter.fromJson(TEST4, char.class));
    }
}
