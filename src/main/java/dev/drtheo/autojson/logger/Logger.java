package dev.drtheo.autojson.logger;

public interface Logger {

    void log(String sep, Object... msgs);

    default void log(Object... msgs) {
        log(" ", msgs);
    }

    void log(Object message);

    void warn(String sep, Object... msgs);

    default void warn(Object... msgs) {
        warn(" ", msgs);
    }

    void warn(String message);
}
