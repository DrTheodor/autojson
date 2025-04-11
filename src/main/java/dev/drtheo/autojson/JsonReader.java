package dev.drtheo.autojson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class JsonReader {
    // Token types
    public static final int BEGIN_OBJECT = 1;
    public static final int END_OBJECT = 2;
    public static final int BEGIN_ARRAY = 3;
    public static final int END_ARRAY = 4;
    public static final int NAME = 5;
    public static final int STRING = 6;
    public static final int NUMBER = 7;
    public static final int BOOLEAN = 8;
    public static final int NULL = 9;
    public static final int EOF = 10;

    private final Reader reader;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private String currentName;
    private boolean expectingValue = false;

    // Current token state
    private int currentToken;
    private String stringValue;
    private double numberValue;
    private boolean booleanValue;

    // Buffer and parsing state
    private char[] buffer = new char[8192];
    private int pos = 0;
    private int limit = 0;
    private int currentChar = ' ';
    private int line = 1;
    private int lineStart = 0;

    // For peek functionality
    private int peekedToken = -1;

    public JsonReader(Reader reader) {
        this.reader = reader;
    }

    public JsonReader(String json) {
        this(new StringReader(json));
    }

    public int peek() throws IOException {
        if (peekedToken == -1) {
            peekedToken = nextToken();
        }
        return peekedToken;
    }

    public void beginObject() throws IOException {
        int token = nextToken();
        if (token != BEGIN_OBJECT) {
            throw new IllegalStateException("Expected begin object");
        }
        stack.push(BEGIN_OBJECT);
    }

    public void endObject() throws IOException {
        int token = nextToken();
        if (token != END_OBJECT) {
            throw new IllegalStateException("Expected end object");
        }
        if (stack.isEmpty() || stack.pop() != BEGIN_OBJECT) {
            throw new IllegalStateException("Not inside an object");
        }
    }

    public void beginArray() throws IOException {
        int token = nextToken();
        if (token != BEGIN_ARRAY) {
            throw new IllegalStateException("Expected begin array");
        }
        stack.push(BEGIN_ARRAY);
    }

    public void endArray() throws IOException {
        int token = nextToken();
        if (token != END_ARRAY) {
            throw new IllegalStateException("Expected end array");
        }
        if (stack.isEmpty() || stack.pop() != BEGIN_ARRAY) {
            throw new IllegalStateException("Not inside an array");
        }
    }

    public String name() throws IOException {
        if (stack.isEmpty() || stack.peek() != BEGIN_OBJECT) {
            throw new IllegalStateException("Not inside an object");
        }
        if (expectingValue) {
            // We already have a name, need to get its value first
            skipValue();
        }

        int token = nextToken();
        if (token != NAME) {
            throw new IllegalStateException("Expected field name");
        }
        currentName = stringValue;
        expectingValue = true;
        return currentName;
    }

    public void skipValue() throws IOException {
        if (!expectingValue) {
            return;
        }

        int token = nextToken();
        switch (token) {
            case BEGIN_OBJECT:
                beginObject();
                while (peek() != END_OBJECT) {
                    name();
                    skipValue();
                }
                endObject();
                break;
            case BEGIN_ARRAY:
                beginArray();
                while (peek() != END_ARRAY) {
                    skipValue();
                }
                endArray();
                break;
            case STRING:
            case NUMBER:
            case BOOLEAN:
            case NULL:
                // Just consume the value
                break;
            default:
                throw new IllegalStateException("Expected value");
        }
        expectingValue = false;
    }

    public Object value() throws IOException {
        if (!stack.isEmpty() && stack.peek() == BEGIN_OBJECT && !expectingValue) {
            name(); // Automatically read the name if we're in an object
        }

        int token = nextToken();
        Object value;

        switch (token) {
            case STRING:
                value = stringValue;
                break;
            case NUMBER:
                value = numberValue;
                break;
            case BOOLEAN:
                value = booleanValue;
                break;
            case NULL:
                value = null;
                break;
            case BEGIN_OBJECT:
                beginObject();
                value = null; // The object itself is handled by beginObject()
                break;
            case BEGIN_ARRAY:
                beginArray();
                value = null; // The array itself is handled by beginArray()
                break;
            default:
                throw new IllegalStateException("Expected value");
        }

        expectingValue = false;
        return value;
    }

    private int nextToken() throws IOException {
        if (peekedToken != -1) {
            int token = peekedToken;
            peekedToken = -1;
            return token;
        }

        while (true) {
            skipWhitespace();

            switch (currentChar) {
                case '{':
                    advance();
                    return BEGIN_OBJECT;

                case '}':
                    advance();
                    return END_OBJECT;

                case '[':
                    advance();
                    return BEGIN_ARRAY;

                case ']':
                    advance();
                    return END_ARRAY;

                case ':', ',':
                    advance();
                    continue;

                case '"':
                    stringValue = parseString();
                    return (!stack.isEmpty() && stack.peek() == BEGIN_OBJECT && !expectingValue) ? NAME : STRING;

                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    numberValue = parseNumber();
                    return NUMBER;

                case 't':
                    expect("true");
                    booleanValue = true;
                    return BOOLEAN;

                case 'f':
                    expect("false");
                    booleanValue = false;
                    return BOOLEAN;

                case 'n':
                    expect("null");
                    return NULL;

                case -1:
                    return EOF;

                default:
                    throw syntaxError("Unexpected character: " + (char) currentChar);
            }
        }
    }

    private void expect(String expected) throws IOException {
        for (int i = 0; i < expected.length(); i++) {
            if (currentChar != expected.charAt(i)) {
                throw syntaxError("Expected " + expected);
            }
            advance();
        }
    }

    private void skipWhitespace() throws IOException {
        while (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
            advance();
        }
    }

    private void advance() throws IOException {
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

    private void fillBuffer() throws IOException {
        limit = reader.read(buffer);
        pos = 0;
    }

    private IOException syntaxError(String message) {
        return new IOException(message + " at line " + line + " column " + (pos - lineStart));
    }

    private String parseString() throws IOException {
        advance(); // consume opening quote
        StringBuilder builder = null;
        int start = pos - 1;

        while (currentChar != '"') {
            if (currentChar == '\\') {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(buffer, start, pos - 1 - start);
                advance(); // consume backslash
                switch (currentChar) {
                    case '"':
                    case '\\':
                    case '/':
                        builder.append((char) currentChar);
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'u':
                        builder.append(parseUnicode());
                        break;
                    default:
                        throw syntaxError("Invalid escape sequence");
                }
                advance();
                start = pos - 1;
            } else if (currentChar < ' ') {
                throw syntaxError("Unterminated string");
            } else {
                advance();
            }
        }

        String result;
        if (builder == null) {
            result = new String(buffer, start, pos - 1 - start);
        } else {
            builder.append(buffer, start, pos - 1 - start);
            result = builder.toString();
        }

        advance(); // consume closing quote
        return result;
    }

    private char parseUnicode() throws IOException {
        int codePoint = 0;
        for (int i = 0; i < 4; i++) {
            advance();
            int digit = Character.digit(currentChar, 16);
            if (digit == -1) {
                throw syntaxError("Invalid Unicode escape sequence");
            }
            codePoint = (codePoint << 4) + digit;
        }
        return (char) codePoint;
    }

    private double parseNumber() throws IOException {
        StringBuilder builder = new StringBuilder();

        if (currentChar == '-') {
            builder.append('-');
            advance();
        }

        while (currentChar >= '0' && currentChar <= '9') {
            builder.append((char) currentChar);
            advance();
        }

        if (currentChar == '.') {
            builder.append('.');
            advance();
            while (currentChar >= '0' && currentChar <= '9') {
                builder.append((char) currentChar);
                advance();
            }
        }

        if (currentChar == 'e' || currentChar == 'E') {
            builder.append('e');
            advance();
            if (currentChar == '+' || currentChar == '-') {
                builder.append((char) currentChar);
                advance();
            }
            while (currentChar >= '0' && currentChar <= '9') {
                builder.append((char) currentChar);
                advance();
            }
        }

        try {
            return Double.parseDouble(builder.toString());
        } catch (NumberFormatException e) {
            throw syntaxError("Malformed number");
        }
    }

    // ... [remaining helper methods stay the same] ...

    public static void main(String[] args) throws IOException {
        String json = "{\"name\":\"John\",\"age\":30,\"isActive\":true,\"address\":null,\"scores\":[90,85,95],\"profile\":{\"height\":180,\"weight\":75}}";

        JsonReader parser = new JsonReader(json);

        parser.beginObject();
        while (parser.peek() != JsonReader.END_OBJECT) {
            String name = parser.name();

            switch (parser.peek()) {
                case JsonReader.BEGIN_OBJECT:
                    System.out.println(name + ":");
                    parser.beginObject();
                    while (parser.peek() != JsonReader.END_OBJECT) {
                        String field = parser.name();
                        Object value = parser.value();
                        System.out.println("  " + field + ": " + value);
                    }
                    parser.endObject();
                    break;

                case JsonReader.BEGIN_ARRAY:
                    System.out.print(name + ": ");
                    parser.beginArray();
                    while (parser.peek() != JsonReader.END_ARRAY) {
                        System.out.print(parser.value() + " ");
                    }
                    parser.endArray();
                    System.out.println();
                    break;

                default:
                    System.out.println(name + ": " + parser.value());
                    break;
            }
        }
        parser.endObject();
    }
}