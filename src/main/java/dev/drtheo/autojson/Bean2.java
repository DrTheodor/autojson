package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.string.JsonStringAdapter;

import java.util.*;

public class Bean2 {

    final String[] strArray = new String[] {
            "str1", "str2", "str3"
    };

    final List<String> strList = new ArrayList<>() {{
        add("str4");
        add("str5");
        add("str6");
    }};

//    final Set<String> strSet = new HashSet<>() {{
//        add("str1");
//        add("str2");
//        add("str3");
//    }};


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bean2{\n").append("\tstrArray=[");

        for (String s : strArray) {
            System.out.println(s);
//            builder.append(s).append(",");
        }

        return builder.append("], strList=").append(strList).append("}").toString();
    }

    public static void main(String[] args) {
        Bean2 bean = new Bean2();

        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        System.out.println(adapter.toJson(bean));
        System.out.println(adapter.fromJson("{\"strArray\":[\"str1\",\"str2\",\"str3\"],\"strList\":[\"str4\",\"str5\",\"str6\"]}", Bean2.class));

    }
}
