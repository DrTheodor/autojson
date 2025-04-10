package dev.drtheo.autojson.adapter.string;

import java.io.Serial;

public final class JsonSyntaxException extends JsonParseException {
    @Serial
    private static final long serialVersionUID = 1L;

    public JsonSyntaxException(String msg) {
        super(msg);
    }

    public JsonSyntaxException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JsonSyntaxException(Throwable cause) {
        super(cause);
    }
}
