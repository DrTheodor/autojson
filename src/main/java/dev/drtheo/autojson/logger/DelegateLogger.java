package dev.drtheo.autojson.logger;

public interface DelegateLogger extends Logger {
    
    Logger logger();

    @Override
    default void log(String sep, Object... msgs) {
        this.logger().log(sep, msgs);
    }

    @Override
    default void log(Object message) {
        this.logger().log(message);
    }

    @Override
    default void warn(String sep, Object... msgs) {
        this.logger().warn(sep, msgs);
    }

    @Override
    default void warn(String message) {
        this.logger().warn(message);
    }
}
