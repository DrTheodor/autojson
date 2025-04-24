package dev.drtheo.autojson.adapter.string.parser2;

import static dev.drtheo.autojson.util.UnsafeUtil.UNSAFE;

public class AbstractJsonReader {

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
    protected final ThreadLocal<StringBuilder> stringBuilder =
            ThreadLocal.withInitial(() -> new StringBuilder(32));

    public AbstractJsonReader(byte[] jsonBytes) {
        this.buffer = jsonBytes;
    }

    public Object parsePrimitive() {
        return switch (buffer[position]) {
            case QUOTE -> parseString();
            case 't' -> parseTrue();
            case 'f' -> parseFalse();
            case 'n' -> parseNull();
            default -> parseNumber();
        };
    }

    private double parseDoublePrecise(int start, int length, boolean negative) {
        // Reuse thread-local StringBuilder to avoid allocations
        StringBuilder sb = stringBuilder.get();
        sb.setLength(0);

        // Unsafe copy of the number characters
        for (int i = 0; i < length; i++) {
            sb.append((char) UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + start + i));
        }

        // Use Java's built-in parser for perfect accuracy
        double result = Double.parseDouble(sb.toString());
        return negative ? -result : result;
    }

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

    private int parseIntUnsafe(int start, int length, boolean negative) {
        int result = 0;
        int i = start;

        while (i < start + length) {
            byte b = UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + i);
            if (b < '0' || b > '9') {
                throw new RuntimeException("Invalid integer format at position " + i);
            }
            result = result * 10 + (b - '0');
            i++;
        }

        return negative ? -result : result;
    }

    private Number parseNumber() {
        boolean isDouble = false;
        boolean hasNotation = false;

        byte firstByte = UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + position);
        boolean negative = firstByte == '-';
        position++;

        int start = position;

        while (position < buffer.length) {
            byte b = buffer[position];

            if (b == '.')
                isDouble = true;
            else if (b == 'e' || b == 'E') {
                hasNotation = true;
                isDouble = true;
            } else if (!((b >= '0' && b <= '9') || b == '-' || b == '+')) {
                break;
            }

            position++;
        }

        int length = position - start;
        if (length == 0) {
            throw new RuntimeException("Expected number at position " + position);
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

    public void parseArray() {
        position++; // skip '['
        skipWhitespace();

        if (buffer[position] == RBRACKET) { // empty array
            position++;
            onEndArray();
            return;
        }

        fillArray();
        onEndArray();
    }

    private void fillArray() {
        int index = 0;
        while (true) {
            skipWhitespace();
            onElement(index++);
            skipWhitespace();

            byte b = buffer[position];
            if (b == RBRACKET) {
                position++;
                return;
            }
            if (b != COMMA) {
                throw new RuntimeException("Expected ',' or ']' at position " + position);
            }
            position++;
        }
    }

    protected void onEndArray() {

    }

    protected void onElement(int i) {

    }

    public boolean parseTrue() {
        if (position + 3 >= buffer.length &&
                buffer[position] == 't' &&
                buffer[position+1] == 'r' &&
                buffer[position+2] == 'u' &&
                buffer[position+3] == 'e') {
            position += 4;
            return true;
        }

        throw new IllegalStateException("Expected 'true' but got got '" + buffer[position] + "' at pos " + position);
    }

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

    public void skipWhitespace() {
        while (position < buffer.length && isWhitespace(buffer[position])) {
            position++;
        }
    }

    private String parseString() {
        position++; // skip opening quote
        int start = position;

        while (position < buffer.length && buffer[position] != QUOTE) {
            position++;
        }

        if (position >= buffer.length)
            throw new RuntimeException("Unclosed string at " + position);

        int length = position - start;
        position++; // skip closing quote

        // Unsafe copy remains the same for both approaches
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + start + i);
        }
        return new String(chars);
    }

    protected void expect(byte c) {
        if (buffer[position] != c)
            throw new IllegalStateException("Expected '" + c + "' at " + position + " but got '" + buffer[position] + "'");
    }

    protected void parseObject() {
        expect(LBRACE);
        position++;

        while (true) {
            skipWhitespace();
            if (buffer[position] == RBRACE) {
                position++;
                onEndObject();
                return;
            }

            this.onKey(parseKey());

            skipWhitespace();

            if (buffer[position] == COMMA) {
                position++;
            } else if (buffer[position] == RBRACE) {
                position++;
                onEndObject();
                return;
            } else {
                throw new RuntimeException("Expected ',' or '}'");
            }
        }
    }

    protected void onEndObject() {

    }

    protected void onKey(String key) {

    }

    public String parseKey() {
        String key = parseString();
        skipWhitespace();
        expect(COLON);
        position++;
        skipWhitespace();

        return key;
    }

    protected boolean isWhitespace(byte b) {
        return b == ' ' || b == '\n' || b == '\r' || b == '\t';
    }
}