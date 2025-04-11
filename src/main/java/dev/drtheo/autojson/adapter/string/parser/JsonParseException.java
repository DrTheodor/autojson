package dev.drtheo.autojson.adapter.string.parser;

import java.io.Serial;

public class JsonParseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4086729973971783390L;

    public JsonParseException(String msg) {
        super(msg);
    }

    public JsonParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }
}