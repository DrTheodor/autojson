package dev.drtheo.autojson.adapter.string.parser2;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.schema.base.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JsonStringParser2 extends AbstractJsonReader implements JsonDeserializationContext {

    private final JsonStringAdapter adapter;

    private Node<?, ?> node;

    public JsonStringParser2(JsonStringAdapter adapter, String raw) {
        super(raw.getBytes(StandardCharsets.UTF_8));
        this.adapter = adapter;
    }

    @Override
    public <T> T decodeBuiltIn() {
        return (T) parsePrimitive();
    }

    @Override
    public <T> T decodeCustom(Type type, @NotNull Schema<T> schema) {
        if (schema.type() == SchemaType.OBJECT) {
            ObjectNode<T> n = new ObjectNode<>(schema.asObject(), this.node);

            this.node = n;
            parseObject();

            return n.val;
        }

        if (schema.type() == SchemaType.ARRAY) {
            ArrayNode<T, ?> n = new ArrayNode<>(schema.asArray(), this.node);

            this.node = n;
            parseArray();

            return n.pack();
        }

        if (schema.type() == SchemaType.PRIMITIVE)
            return schema.<T>asPrimitive().deserialize(adapter, this);

        if (schema.type() == SchemaType.WRAPPER)
            return decodeWrapper(schema.asWrapper());

        return null;
    }

    @Override
    protected void onElement(int i) {
        ((ArrayNode<?, ?>) node).decode(adapter, this, i);
    }

    @Override
    protected void onEndArray() {
        if (node.parent != null)
            node = node.parent;
    }

    private <T, B> T decodeWrapper(WrapperSchema<T, B> schema) {
        return schema.deserialize(adapter, this.decode(schema.wrapping(), schema.child()));
    }

    @Override
    protected void onKey(String key) {
        ((ObjectNode<?>) node).decode(adapter, this, key);
    }

    @Override
    protected void onEndObject() {
        if (node.parent != null)
            node = node.parent;
    }

    @Override
    public AutoJSON auto() {
        return adapter.auto();
    }

    static abstract class Node<T extends Schema<?>, V> {
        Node<?, ?> parent;
        T schema;
        V val;

        public Node(T schema, Node<?, ?> parent) {
            this.parent = parent;
            this.schema = schema;
        }
    }

    static class ObjectNode<T> extends Node<ObjectSchema<T>, T> {

        public ObjectNode(ObjectSchema<T> schema, Node<?, ?> parent) {
            super(schema, parent);
            this.val = schema.instantiate();
        }

        public <To> void decode(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, String key) {
            schema.deserialize(adapter, ctx, val, key);
        }
    }

    static class ArrayNode<T, Intr> extends Node<ArraySchema<T, Intr>, Intr> {

        public ArrayNode(ArraySchema<T, Intr> schema, Node<?, ?> parent) {
            super(schema, parent);
            val = schema.instantiate();
        }

        public <To> void decode(JsonAdapter<Object, To> adapter, JsonDeserializationContext ctx, int i) {
            schema.deserialize(adapter, ctx, val, i);
        }

        public T pack() {
            return schema.pack(val);
        }
    }

    public static void main(String[] args) {
        String raw = "{\"another\":{\"key\":\"test\",\"ints\":[1,2,3]},\"doubleArray\":[[\"test\", \"wow\", \"this\"], [\"sucks\", \"so\", \"much\"]]}";
        JsonStringAdapter adapter1 = new JsonStringAdapter(new AutoJSON());
        JsonStringParser2 parser2 = new JsonStringParser2(adapter1, raw);

        System.out.println(parser2.<Raw>decodeCustom(Raw.class));
    }

    static class Raw {
        Raw2 another;
        String[][] doubleArray;

        @Override
        public String toString() {
            if (doubleArray != null) {
                for (String[] s : doubleArray) {
                    System.out.println(Arrays.toString(s));
                }
            }
            return "Raw{" +
                    "another=" + another +
                    ", doubleArray=" + Arrays.toString(doubleArray) +
                    '}';
        }
    }

    static class Raw2 {
        String key;
        int[] ints;

        @Override
        public String toString() {
            return "Raw2{" +
                    "key='" + key + '\'' +
                    ", ints=" + Arrays.toString(ints) +
                    '}';
        }
    }
}
