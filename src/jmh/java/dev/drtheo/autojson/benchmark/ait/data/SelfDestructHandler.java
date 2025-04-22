package dev.drtheo.autojson.benchmark.ait.data;

public class SelfDestructHandler extends TardisComponent {
    boolean queued;

    public SelfDestructHandler() {
        super(IdLike.SELF_DESTRUCT);
    }
}
