package dev.drtheo.autojson.benchmark.beans;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;

import java.util.*;

public class Bean2 {

    final int[] intArray = new int[] {
            -2, -1, 0
    };

    final String[] strArray = new String[] {
            "str1", "str2", "str3"
    };

    final List<String> strList = new ArrayList<>() {{
        add("str4");
        add("str5");
        add("str6");
    }};

    final Set<String> strSet = new HashSet<>() {{
        add("str1");
        add("str2");
        add("str3");
    }};


    @Override
    public String toString() {
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder builder = new StringBuilder();
        builder.append("Bean2 {\n");

        builder.append("\tintArray=").append(Arrays.toString(intArray)).append(",\n");
        builder.append("\tstrArray=").append(Arrays.toString(strArray)).append(",\n");
        builder.append("\tstrList=").append(strList).append(",\n");
        builder.append("\tstrSet=").append(strSet).append(",\n");

        return builder.append("}").toString();
    }

    public static void main(String[] args) {
        Bean2 bean = new Bean2();

        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        System.out.println(adapter.toJson(bean));

        Bean2 b = adapter.fromJson("{\"intArray\":[-2, -1, 0],\"strArray\":[\"str1\",\"str2\",\"str3\"],\"strList\":[\"str4\",\"str5\",\"str6\"],\"strSet\":[\"str7\", \"str8\",\"str9\"]}", Bean2.class);
        System.out.println(b);
    }
}
