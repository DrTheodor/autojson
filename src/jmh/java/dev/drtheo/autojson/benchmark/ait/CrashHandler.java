package dev.drtheo.autojson.benchmark.ait;

public class CrashHandler {

    State state = State.NORMAL;
    int repairTicks;

    enum State {
        NORMAL, UNSTABLE, TOXIC
    }
}
