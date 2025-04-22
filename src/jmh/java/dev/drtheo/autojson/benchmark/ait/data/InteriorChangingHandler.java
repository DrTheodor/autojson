package dev.drtheo.autojson.benchmark.ait.data;

public class InteriorChangingHandler extends TardisComponent {
    int plasmicMaterialAmount;
    boolean hasCage;
    Id queuedInterior = new Id("ait", "hourglass");
    boolean queued;
    boolean regenerating;

    public InteriorChangingHandler() {
        super(IdLike.INTERIOR_CHANGING);
    }
}
