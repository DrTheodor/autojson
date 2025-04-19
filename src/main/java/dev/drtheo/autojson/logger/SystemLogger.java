package dev.drtheo.autojson.logger;

public class SystemLogger implements Logger {

    private void logAny(String prefix, String sep, Object... msgs) {
        System.out.println("[");
        System.out.print(prefix);
        System.out.print("] ");

        if (msgs.length != 0) {
            for (int i = 1; i < msgs.length - 1; i++) {
                System.out.print(msgs[i]);
                System.out.print(sep);
            }

            System.out.print(msgs[msgs.length - 1]);
        }
    }

    @Override
    public void log(String sep, Object... msgs) {
        this.logAny("INFO", sep, msgs);
    }

    @Override
    public void log(Object message) {
        System.out.println("[INFO] ");
        System.out.println(message);
    }

    @Override
    public void warn(String sep, Object... msgs) {
        this.logAny("WARN", sep, msgs);
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARN] ");
        System.out.print(message);
    }
}
