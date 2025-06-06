package dev.drtheo.autojson.benchmark.comparison.beans;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
public class Bean {
    private int primInt = Integer.MAX_VALUE;
    private boolean primBool = true;
    private byte primByte = Byte.MAX_VALUE;
    private char primChar = Character.MAX_VALUE;
    private short primShort = Short.MAX_VALUE;
    private double primDouble = Double.MAX_VALUE;
    private float primFloat = Float.MAX_VALUE;
    private long primLong = Long.MAX_VALUE;

    private final int[] intArr = new int[] {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 0
    };

    private final String[] strArr = new String[]{
            "hippity", "hoppity"
    };

    private final List<String> strList = new ArrayList<>() {{
        add("never gonna give you up");
        add("never gonna let you down");
    }};

    private final Integer intObj = Integer.MAX_VALUE;
    private final Boolean boolObj = true;
    private final Byte byteObj = Byte.MAX_VALUE;
    private final Character charObj = Character.MAX_VALUE;
    private final Short shortObj = Short.MAX_VALUE;
    private final Double doubleObj = Double.MAX_VALUE;
    private final Float floatObj = Float.MAX_VALUE;
    private final Long longObj = Long.MAX_VALUE;

    private String hello = "HELLO MATE";
    private final Id id = new Id("ait", "whatever");
    //private Sound sound = new Sound(id);

    static class Id {
        private final String namespace;
        private final String path;

        public Id(String namespace, String path) {
            this.namespace = namespace;
            this.path = path;
        }

        @Override
        public String toString() {
            return "Id{" +
                    "namespace='" + namespace + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }
    }

    @SuppressWarnings("unused")
    record Sound(Id id) {

    }

    @Override
    public String toString() {
        return "Bean{" +
                "primInt=" + primInt +
                ", primBool=" + primBool +
                ", primByte=" + primByte +
                ", primChar=" + primChar +
                ", primShort=" + primShort +
                ", primDouble=" + primDouble +
                ", primFloat=" + primFloat +
                ", primLong=" + primLong +
                ", intArr=" + Arrays.toString(intArr) +
                ", strArr=" + Arrays.toString(strArr) +
                ", strList=" + strList +
                ", intObj=" + intObj +
                ", boolObj=" + boolObj +
                ", byteObj=" + byteObj +
                ", charObj=" + charObj +
                ", shortObj=" + shortObj +
                ", doubleObj=" + doubleObj +
                ", floatObj=" + floatObj +
                ", longObj=" + longObj +
                ", hello='" + hello + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Bean bean = (Bean) object;
        return primInt == bean.primInt && primBool == bean.primBool && primByte == bean.primByte && primChar == bean.primChar && primShort == bean.primShort && Double.compare(primDouble, bean.primDouble) == 0 && Float.compare(primFloat, bean.primFloat) == 0 && primLong == bean.primLong && Objects.equals(intObj, bean.intObj) && Objects.equals(boolObj, bean.boolObj) && Objects.equals(byteObj, bean.byteObj) && Objects.equals(charObj, bean.charObj) && Objects.equals(shortObj, bean.shortObj) && Objects.equals(doubleObj, bean.doubleObj) && Objects.equals(floatObj, bean.floatObj) && Objects.equals(longObj, bean.longObj) && Objects.equals(hello, bean.hello) && Objects.equals(id, bean.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primInt, primBool, primByte, primChar, primShort, primDouble, primFloat, primLong, intObj, boolObj, byteObj, charObj, shortObj, doubleObj, floatObj, longObj, hello, id);
    }

    public static void main(String[] args) {
        AutoJSON auto = new AutoJSON();
        JsonStringAdapter adapter = new JsonStringAdapter(auto);

        Bean bean = new Bean();

        String raw = """
                {
                    "primInt":2147483647,
                    "primBool":true,
                    "primByte":127,
                    "primChar":"￿",
                    "primShort":32767,"primDouble":1.7976931348623157E308,"primFloat":3.4028235E38,"primLong":9223372036854775807, "intObj":2147483647,"boolObj":true,"byteObj":127,"charObj":"￿","shortObj":32767,"doubleObj":1.7976931348623157E308,"floatObj":3.4028235E38,"longObj":9223372036854775807,"hello":"HELLO MATE","id":{"namespace":"ait","path":"whatever"}}""";

        for (int i = 0; i < 1_000_000; i++) {
            adapter.fromJson(raw, Bean.class);
        }

        for (int i = 0; i < 1_000_000; i++) {
            adapter.toJson(bean, Bean.class);
        }
    }
}
