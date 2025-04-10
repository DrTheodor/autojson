package dev.drtheo.autojson.ast;

import java.util.Objects;

public class JsonPrimitive implements JsonElement {

    protected final Object value;

    public JsonPrimitive(Boolean bool) {
        value = Objects.requireNonNull(bool);
    }

    public JsonPrimitive(Number number) {
        value = Objects.requireNonNull(number);
    }

    public JsonPrimitive(String string) {
        value = Objects.requireNonNull(string);
    }

    public JsonPrimitive(Character c) {
        value = Objects.requireNonNull(c);
    }

    public boolean getAsBoolean() {
        return (Boolean) value;
    }

    public Number getAsNumber() {
        return (Number) value;
    }

    public String getAsString() {
        return (String) value;
    }

    public Character getAsCharacter() {
        return this.getAsString().charAt(0);
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public Object unwrap() {
        return value;
    }
}
