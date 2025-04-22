package dev.drtheo.autojson.benchmark.ait.data;

public class ShieldHandler extends TardisComponent {
    boolean isShielded;
    boolean isVisuallyShielded;
    int shieldAmbienceTicks;

    public ShieldHandler() {
        super(IdLike.SHIELDS);
    }
}
