package dev.drtheo.autojson.adapter.string.parser;

public interface TokenType {
    byte BEGIN_OBJECT = 0x00;
    byte END_OBJECT = 0x01;
    byte BEGIN_ARRAY = 0x02;
    byte END_ARRAY = 0x03;
    byte NAME = 0x04;
    byte STRING = 0x05;
    byte NUMBER = 0x06;
    byte BOOL = 0x07;
    byte NULL = 0x08;

    static String from(byte raw) {
        return switch (raw) {
            case BEGIN_OBJECT -> "BEGIN_OBJECT";
            case END_OBJECT -> "END_OBJECT";
            case BEGIN_ARRAY -> "BEGIN_ARRAY";
            case END_ARRAY -> "END_ARRAY";
            case NAME -> "NAME";
            case STRING -> "STRING";
            case NUMBER -> "NUMBER";
            case BOOL -> "BOOL";
            case NULL -> "NULL";
            default -> throw new IllegalArgumentException(String.valueOf(raw));
        };
    }
}