package dev.drtheo.autojson.benchmark.comparison.ait.data;

public class WaypointHandler extends TardisComponent {
    boolean hasCartridge;
    AbsoluteBlockPos waypoint = new AbsoluteBlockPos();

    public WaypointHandler() {
        super(IdLike.WAYPOINT);
    }
}
