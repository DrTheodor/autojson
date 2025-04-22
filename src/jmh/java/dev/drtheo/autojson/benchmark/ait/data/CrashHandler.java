package dev.drtheo.autojson.benchmark.ait.data;

public class CrashHandler extends TardisComponent {

    State state = State.NORMAL;
    int repairTicks;

    public CrashHandler() {
        super(IdLike.CRASH);
    }

    enum State {
        NORMAL, UNSTABLE, TOXIC
    }
}
