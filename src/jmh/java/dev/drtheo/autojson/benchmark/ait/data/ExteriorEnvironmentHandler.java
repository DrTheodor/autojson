package dev.drtheo.autojson.benchmark.ait.data;

public class ExteriorEnvironmentHandler extends TardisComponent {

    boolean raining;
    boolean thundering;
    boolean lava;

    public ExteriorEnvironmentHandler() {
        super(IdLike.ENVIRONMENT);
    }
}
