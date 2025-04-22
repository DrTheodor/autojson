package dev.drtheo.autojson.benchmark.ait.data;

public class ServerAlarmHandler extends TardisComponent {
    boolean enabled;
    boolean hostilePresence;

    public ServerAlarmHandler() {
        super(IdLike.ALARMS);
    }
}
