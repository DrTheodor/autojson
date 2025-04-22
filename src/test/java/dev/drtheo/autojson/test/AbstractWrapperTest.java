package dev.drtheo.autojson.test;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.SchemaHolder;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.JsonSerializationContext;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.schema.base.PrimitiveSchema;
import dev.drtheo.autojson.schema.base.Schema;
import dev.drtheo.autojson.schema.base.WrapperSchema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class AbstractWrapperTest {

    public static final AutoJSON auto = new AutoJSON();
    public static final JsonStringAdapter adapter = new JsonStringAdapter(auto);

    static final MyWrapper<MyData> wrapperField = null;
    public static final Type wrapperType;

    public static MyWrapper<MyData> wrapper = new MyWrapper<>();

    public static MyWrapperWrapper wrapperWrapper = new MyWrapperWrapper();

    static {
        auto.schema(MyData.class, new MyDataSchema());
        auto.template(MyWrapper.class, MyWrapperSchema::new);

        try {
            wrapperType = AbstractWrapperTest.class.getDeclaredField("wrapperField").getGenericType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        wrapper.t = new MyData();
        wrapperWrapper.wrapper = wrapper;
    }

    public static class MyData {
        public String test = "hi";

        @Override
        public String toString() {
            return "MyData{" +
                    "test='" + test + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyData data1)
                return Objects.equals(this.test, data1.test);

            return false;
        }
    }

    public static class MyWrapper<T> {
        public T t;

        @Override
        public String toString() {
            return "MyWrapper{" +
                    "t=" + t +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyWrapper<?> wrapper1)
                return Objects.equals(this.t, wrapper1.t);

            return false;
        }
    }

    public static class MyWrapperWrapper {
        public MyWrapper<MyData> wrapper;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MyWrapperWrapper wrapper1)
                return Objects.equals(this.wrapper, wrapper1.wrapper);

            return false;
        }
    }

    public static class MyDataSchema implements PrimitiveSchema<MyData> {

        @Override
        public <To> void serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext.Primitive ctx, MyData myData) {
            ctx.primitive$value(myData.test);
        }

        @Override
        public <To> MyData deserialize(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx) {
            MyData data = new MyData();
            data.test = ctx.decodeBuiltIn();
            return data;
        }
    }

    public static class MyWrapperSchema<T> implements WrapperSchema<MyWrapper<T>, T> {

        private final Type wrapping;
        private final Schema<T> wrapper;

        public MyWrapperSchema(SchemaHolder holder, ParameterizedType type) {
            this.wrapping = type.getActualTypeArguments()[0];
            this.wrapper = holder.schema(wrapping);
        }

        @Override
        public <To> JsonSerializationContext.Built serialize(JsonAdapter<Object, To> adapter, JsonSerializationContext ctx, MyWrapper<T> tMyWrapper) {
            return child().serialize(adapter, ctx, tMyWrapper.t);
        }

        @Override
        public Type wrapping() {
            return wrapping;
        }

        @Override
        public <To> MyWrapper<T> deserialize(JsonAdapter<Object, To> adapter, T t) {
            MyWrapper<T> wrapper = new MyWrapper<>();
            wrapper.t = t;
            return wrapper;
        }

        @Override
        public Schema<T> child() {
            return wrapper;
        }
    }
}
