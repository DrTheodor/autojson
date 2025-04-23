package dev.drtheo.autojson.adapter.string.parser;

public class Token {
    public byte type;
    public Object value;

    public Token(byte type, Object value) {
        this.type = type;
        this.value = value;
    }
}