package dev.drtheo.autojson.adapter.string.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class JsonReader {

    private final Reader reader;
    private final char[] buffer = new char[1024];

    private int pos = 0;
    private int limit = 0;
    private int currentChar = ' ';
    private int line = 1;
    private int lineStart = 0;

    private boolean expectsValue = true;
    private boolean isMapped = false;

    private int depth;
    private final Boolean[] objectStack = new Boolean[32];

    public JsonReader(String raw) {
        this.reader = new StringReader(raw);
        this.advance();
    }

    private void fillBuffer() {
        try {
            limit = reader.read(buffer);
            pos = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void advance() {
        if (pos == limit) {
            fillBuffer();
        }

        if (limit == -1) {
            currentChar = -1;
            return;
        }

        currentChar = buffer[pos++];

        if (currentChar == '\n') {
            line++;
            lineStart = pos;
        }
    }

    public enum TokenType {
        BEGIN_OBJECT,
        END_OBJECT,
        BEGIN_ARRAY,
        END_ARRAY,
        NAME,
        STRING,
        NUMBER,
        BOOL,
        NULL,
    }

    public record Token(TokenType type, Object value) { }

    public boolean hasNext() {
        return this.currentChar != -1;
    }

    public Token nextToken() {
        while (true) {
            skipWhitespace();

            if (!this.hasNext())
                throw syntaxError("EOF");

            if (this.currentChar == ',' || this.currentChar == ':') {
                this.advance();
                continue;
            }

            if (!this.isMapped || !this.expectsValue) {
                if (this.currentChar == '}') {
                    this.endObject();
                    return new Token(TokenType.END_OBJECT, null);
                }

                if (this.currentChar == ']') {
                    this.endArray();
                    return new Token(TokenType.END_ARRAY, null);
                }
            }

            if (this.expectsValue) {
                Token token = switch (this.currentChar) {
                    case '{' -> {
                        this.beginObject();
                        yield new Token(TokenType.BEGIN_OBJECT, null);
                    }

                    case '[' -> {
                        this.beginArray();
                        yield new Token(TokenType.BEGIN_ARRAY, null);
                    }

                    case '"' -> new Token(TokenType.STRING, this.parseString());

                    case 't', 'f' -> new Token(TokenType.BOOL, this.parseBoolean());
                    case 'n' -> new Token(TokenType.NULL, this.parseNull());

                    case '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                            new Token(TokenType.NUMBER, this.parseNumber());

                    default -> throw syntaxError("Unexpected token '" + this.currentChar + "'");
                };

                this.expectsValue = !isMapped;
                return token;
            } else {
                this.expectsValue = true;
                return new Token(TokenType.NAME, this.parseString());
            }
        }
    }

    private String parseString() {
        consume('"'); // opening quote
        int start = pos - 1;

        while (currentChar != '"') {
            advance();
        }

        String result = new String(buffer, start, pos - 1 - start);

        consume('"'); // closing quote
        return result;
    }

    private Object parseNull() {
        expect("null");
        return null;
    }

    private boolean parseBoolean() {
        if (currentChar == 't') {
            expect("true");
            return true;
        } else if (currentChar == 'f') {
            expect("false");
            return false;
        }

        throw syntaxError("Expected boolean");
    }

    private void expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (currentChar != expected.charAt(i)) {
                throw syntaxError("Expected " + expected);
            }
            advance();
        }
    }

    private LazilyParsedNumber parseNumber() {
        int start = pos - 1;

        while ((currentChar >= '0' && currentChar <= '9') || currentChar == '.'
                || currentChar == 'e' || currentChar == 'E'
                || currentChar == '+' || currentChar == '-') {
            advance();
        }

        String result = new String(buffer, start, pos - 1 - start);
        return new LazilyParsedNumber(result);
    }

    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
            advance();
        }
    }

    private void consume(char c) {
        if (this.currentChar != c)
            throw syntaxError("Expected '" + c + "', got '" + (char) this.currentChar + "'");

        this.advance();
    }

    private void beginObject() {
        this.consume('{');
        this.expectsValue = false;

        this.isMapped = true;

        this.depth++;
        this.objectStack[this.depth] = true;
    }

    private void endObject() {
        this.consume('}');
        this.popStack();
    }

    private void beginArray() {
        this.consume('[');
        this.expectsValue = true;

        this.isMapped = false;

        this.objectStack[this.depth++] = false;
    }

    private void endArray() {
        this.consume(']');
        this.popStack();
    }

    private void popStack() {
        this.objectStack[this.depth--] = null;

        this.isMapped = this.depth > 0 && this.objectStack[this.depth];
        this.expectsValue = !this.isMapped;
    }

    private JsonSyntaxException syntaxError(String message) {
        return new JsonSyntaxException(message + " at line " + line + " column " + (pos - lineStart));
    }

    public static void main(String[] args) {
        String json = "{\"name\":\"John\",\"age\":30,\"isActive\":true,\"address\":null,\"scores\":[90,85,95],\"profile\":{\"height\":180,\"weight\":75}}";

        JsonReader parser = new JsonReader(json);
        Map<String, Object> o = create();

        parser.beginObject();
        deserializeObject(o, parser);

        System.out.println(o);
    }

    private static Object deserializeObject(Map<String, Object> o, JsonReader reader) {
        while (reader.hasNext()) {
            Token name = reader.nextToken();

            if (name.type() == TokenType.END_OBJECT)
                break;

            Token value = reader.nextToken();
            deserializeField((String) name.value, value, o, reader);
        }

        return o;
    }

    private static void deserializeField(String name, Token value, Map<String, Object> o, JsonReader reader) {
        Object v = switch (value.type()) {
            case BEGIN_ARRAY -> deserializeList(reader);
            case BEGIN_OBJECT -> deserializeObject(create(name), reader);
            case NUMBER, STRING, BOOL, NULL -> value.value;

            default -> throw new IllegalStateException(value.type().toString());
        };

        deserialize(o, name, v);
    }

    private static List<Object> deserializeList(JsonReader reader) {
        List<Object> result = new ArrayList<>();

        while (reader.hasNext()) {
            Token t = reader.nextToken();

            if (t.type == TokenType.END_ARRAY)
                return result;

            result.add(t.value);
        }

        return result;
    }

    private static void deserialize(Map<String, Object> obj, String field, Object value) {
        obj.put(field, value);
        System.out.println("Setting field '" + field + "' of " + obj + " to '" + value + "'");
    }

    private static Map<String, Object> create(String field) {
        return new HashMap<>();
    }

    private static Map<String, Object> create() {
        return new HashMap<>();
    }
}
