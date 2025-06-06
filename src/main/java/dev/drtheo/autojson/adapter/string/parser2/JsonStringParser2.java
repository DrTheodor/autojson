package dev.drtheo.autojson.adapter.string.parser2;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;
import dev.drtheo.autojson.schema.base.*;
import dev.drtheo.autojson.util.Lazy;
import jdk.internal.vm.annotation.IntrinsicCandidate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static dev.drtheo.autojson.util.UnsafeUtil.UNSAFE;

public class JsonStringParser2 implements JsonDeserializationContext {

    private static final long BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    // JSON control characters
    protected static final byte QUOTE = '"';
    protected static final byte COLON = ':';
    protected static final byte COMMA = ',';
    protected static final byte LBRACE = '{';
    protected static final byte RBRACE = '}';
    protected static final byte LBRACKET = '[';
    protected static final byte RBRACKET = ']';

    protected final byte[] buffer;
    protected int position;
    protected final Lazy<StringBuilder> numBuilder = new Lazy<>(() -> new StringBuilder(32));

    private final JsonStringAdapter adapter;

    private Node<?, ?> node;

    public JsonStringParser2(JsonStringAdapter adapter, String raw) {
        this.buffer = raw.getBytes(StandardCharsets.UTF_8);
        this.adapter = adapter;
    }

    @Override
    @IntrinsicCandidate
    @SuppressWarnings("unchecked")
    public <T> T decodeBuiltIn() {
        return (T) switch (buffer[position]) {
            case QUOTE -> parseString();
            case 't' -> parseTrue();
            case 'f' -> parseFalse();
            case 'n' -> parseNull();
            default -> parseNumber();
        };
    }

    @IntrinsicCandidate
    private double parseDoublePrecise(int start, int length, boolean negative) {
        StringBuilder sb = numBuilder.get();
        sb.setLength(0);

        // Unsafe copy of the number characters
        for (int i = 0; i < length; i++) {
            sb.append((char) UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + start + i));
        }

        // Use Java's built-in parser for perfect accuracy
        double result = Double.parseDouble(sb.toString());
        return negative ? -result : result;
    }

    @IntrinsicCandidate
    private double fastParseSimpleDouble(int start, int length, boolean negative) {
        int i = start;
        long value = 0;
        int decimalPos = -1;
        int exp = 0;

        // Integer part
        while (i < start + length) {
            byte b = buffer[i];
            if (b == '.') {
                decimalPos = i;
                i++;
                break;
            }
            value = value * 10 + (b - '0');
            i++;
        }

        // Fractional part
        if (decimalPos != -1) {
            while (i < start + length) {
                byte b = buffer[i];
                if (b == 'e' || b == 'E') break;
                value = value * 10 + (b - '0');
                exp--;
                i++;
            }
        }

        // Exponent
        if (i < start + length && (buffer[i] == 'e' || buffer[i] == 'E')) {
            i++;
            boolean expNegative = false;
            if (buffer[i] == '+') i++;
            else if (buffer[i] == '-') {
                expNegative = true;
                i++;
            }

            int expValue = 0;
            while (i < start + length) {
                expValue = expValue * 10 + (buffer[i++] - '0');
            }
            exp += expNegative ? -expValue : expValue;
        }

        // Combine components
        double result = value * Math.pow(10, exp);
        return negative ? -result : result;
    }

    @IntrinsicCandidate
    private int parseIntUnsafe(int start, int length, boolean negative) {
        int result = 0;
        int i = start;

        while (i < start + length) {
            byte b = UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + i);
            if (b < '0' || b > '9') {
                throw new RuntimeException("Invalid integer format at position " + i + "; expected a digit, but got '" + (char) b + "'");
            }
            result = result * 10 + (b - '0');
            i++;
        }

        return negative ? -result : result;
    }

    @IntrinsicCandidate
    private Number parseNumber() {
        boolean isDouble = false;
        boolean hasNotation = false;

        byte firstByte = UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + position);
        boolean negative = false;

        if (firstByte == '+') {
            position++;
        } else if (firstByte == '-') {
            position++;
            negative = true;
        }

        final int start = position;

        while (position < buffer.length) {
            byte b = buffer[position];

            if (b == '.') {
                isDouble = true;
            } else if (b == 'e' || b == 'E') {
                hasNotation = true;
                isDouble = true;
            } else if (!((b >= '0' && b <= '9') || b == '-' || b == '+')) {
                break;
            }

            position++;
        }

        final int length = position - start;
        if (length == 0) {
            throw new RuntimeException("Expected number at " + start + ", but got '" + (char) buffer[position] + "'");
        }

        // Unsafe direct byte access for number parsing
        if (isDouble) {
            if (length < 16 && !hasNotation) {
                return fastParseSimpleDouble(start, length, negative);
            }

            // Fallback to precise parser
            return parseDoublePrecise(start, length, negative);
        } else {
            return parseIntUnsafe(start, length, negative);
        }
    }

    @IntrinsicCandidate
    public boolean parseTrue() {
        if (position + 3 < buffer.length &&
                buffer[position] == 't' &&
                buffer[position+1] == 'r' &&
                buffer[position+2] == 'u' &&
                buffer[position+3] == 'e') {
            position += 4;
            return true;
        }

        throw new IllegalStateException("Expected 'true' but got got '" + new String(buffer, position, 4, StandardCharsets.UTF_8) + "' at pos " + position);
    }

    @IntrinsicCandidate
    public boolean parseFalse() {
        if (position + 4 < buffer.length &&
                buffer[position] == 'f' &&
                buffer[position+1] == 'a' &&
                buffer[position+2] == 'l' &&
                buffer[position+3] == 's' &&
                buffer[position+4] == 'e') {
            position += 5;
            return false;
        }

        throw new IllegalStateException("Expected 'false' but got got '" + (char) buffer[position] + "' at pos " + position);
    }

    @IntrinsicCandidate
    public Object parseNull() {
        if (position + 3 < buffer.length &&
                buffer[position] == 'n' &&
                buffer[position+1] == 'u' &&
                buffer[position+2] == 'l' &&
                buffer[position+3] == 'l') {
            position += 4;
            return null;
        }

        throw new IllegalStateException("Expected 'null' but got got '" + buffer[position] + "' at pos " + position);
    }

    @IntrinsicCandidate
    public void skipWhitespace() {
        while (position < buffer.length && isWhitespace(buffer[position])) {
            position++;
        }
    }

    private boolean isWhitespace(byte b) {
        return b == ' ' || b == '\n' || b == '\r' || b == '\t';
    }

    private String parseString() {
        position++; // skip opening quote
        final int start = position;

        while (position < buffer.length && buffer[position] != QUOTE) {
            position++;
        }

        if (position >= buffer.length)
            throw new RuntimeException("Unclosed string at " + position);

        final int length = position - start;
        consume(QUOTE);

        return new String(buffer, start, length, StandardCharsets.UTF_8);
    }

    @IntrinsicCandidate
    private void expect(byte c) {
        if (buffer[position] != c)
            throw new IllegalStateException("Expected '" + (char) c + "' at " + position + " but got '" + (char) buffer[position] + "'");
    }

    @IntrinsicCandidate
    private void consume(byte c) {
        expect(c);
        position++;
    }

    @IntrinsicCandidate
    protected void parseObject() {
        consume(LBRACE);

        while (true) {
            skipWhitespace();
            if (buffer[position] == RBRACE) {
                position++;
                end();
                return;
            }

            parseKeyValue();
            skipWhitespace();

            if (buffer[position] == COMMA) {
                position++;
            } else if (buffer[position] == RBRACE) {
                position++;
                end();
                return;
            } else {
                throw new RuntimeException("Expected ',' or '}'");
            }
        }
    }

    @IntrinsicCandidate
    public void parseArray() {
        consume(LBRACKET);
        skipWhitespace();

        if (buffer[position] == RBRACKET) { // empty array
            position++;
            end();
            return;
        }

        int index = 0;
        while (true) {
            skipWhitespace();
            ((ArrayNode<?, ?>) node).decode(adapter, this, index++);
            skipWhitespace();

            byte b = buffer[position];
            if (b == RBRACKET) {
                position++;
                end();
                return;
            }

            if (b != COMMA)
                throw new RuntimeException("Expected ',' or ']' at position " + position);

            position++;
        }
    }

    @IntrinsicCandidate
    private void parseKeyValue() {
        String key = parseString();

        skipWhitespace();
        consume(COLON);
        skipWhitespace();

        ((ObjectNode<?>) node).decode(adapter, this, key);
    }

    @IntrinsicCandidate
    private void end() {
        if (node.parent != null)
            node = node.parent;
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

    private <T, B> T decodeWrapper(WrapperSchema<T, B> schema) {
        return schema.deserialize(adapter, this.decode(schema.wrapping(), schema.child()));
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
}
