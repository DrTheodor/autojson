package dev.drtheo.autojson.adapter.string.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
    private final boolean[] objectStack = new boolean[32];

    private boolean lossyNumbers;

    public JsonReader(boolean lossyNumbers, String raw) {
        this.lossyNumbers = lossyNumbers;
        this.reader = new StringReader(raw);
        this.fillBuffer();
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

    public boolean hasNext() {
        return this.currentChar != -1;
    }

    private Token nextToken0() {
        while (true) {
            if (!this.hasNext())
                throw syntaxError("EOF");

            if (this.currentChar == ',' || this.currentChar == ':' || currentChar == ' '
                    || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
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

                    default -> throw syntaxError("Unexpected token '" + (char) this.currentChar + "'");
                };

                this.expectsValue = !isMapped;
                return token;
            } else {
                this.expectsValue = true;
                return new Token(TokenType.NAME, this.parseString());
            }
        }
    }

    public Token nextToken() {
        return this.nextToken0();
    }

    private Object parseNull() {
        expect('n', 'u', 'l', 'l');
        return null;
    }

    private boolean parseBoolean() {
        if (currentChar == 't') {
            expect('t', 'r', 'u', 'e');
            return true;
        } else if (currentChar == 'f') {
            expect('f', 'a', 'l', 's', 'e');
            return false;
        }

        throw syntaxError("Expected boolean");
    }

    private void expect(char... chars) {
        for (char aChar : chars) {
            if (currentChar != aChar)
                throw syntaxError("Expected " + new String(chars));

            advance();
        }
    }

    private String parseString() {
        consume('"'); // opening quote
        int start = pos;

        StringBuilder res = null;

        while (currentChar != '"') {
            if (pos == limit) {
                res = new StringBuilder();
                res.append(buffer, start - 1, pos - start + 1);
                start = 1;
            }

            advance();
        }

        String segment = new String(buffer, start - 1, pos - start);
        String returning;

        if (res != null) {
            res.append(segment);
            returning = res.toString();
        } else {
            returning = segment;
        }

        consume('"'); // closing quote
        return returning;
    }

    private LazilyParsedNumber parseNumber() {
        int start = pos;

        StringBuilder res = null;

        while ((currentChar >= '0' && currentChar <= '9') || currentChar == '.'
                || currentChar == 'e' || currentChar == 'E'
                || currentChar == '+' || currentChar == '-') {
            if (pos == limit) {
                if (res == null)
                    res = new StringBuilder();
                res.append(buffer, start - 1, pos - start + 1);
                start = 1;
            }

            advance();
        }

        String returning;

        if (res != null) {
            if (pos > start)
                res.append(buffer, 0, pos - start);

            returning = res.toString();
        } else {
            returning = new String(buffer, start - 1, pos - start);
        }

        return lossyNumbers ? new LazilyParsedNumber.Lossy(returning) : new LazilyParsedNumber(returning);
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

        this.depth++;
        this.objectStack[this.depth] = false;
    }

    private void endArray() {
        this.consume(']');
        this.popStack();
    }

    private void popStack() {
        this.objectStack[this.depth--] = false;

        this.isMapped = this.depth > 0 && this.objectStack[this.depth];
        this.expectsValue = !this.isMapped;
    }

    private JsonSyntaxException syntaxError(String message) {
        return new JsonSyntaxException(message + " at line " + line + " column " + (pos - lineStart));
    }
}
