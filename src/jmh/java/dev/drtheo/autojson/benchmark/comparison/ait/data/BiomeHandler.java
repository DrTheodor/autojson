package dev.drtheo.autojson.benchmark.comparison.ait.data;

public class BiomeHandler extends TardisComponent {

    BiomeType biome;

    public BiomeHandler() {
        super(IdLike.BIOME);
    }

    enum BiomeType {
        DEFAULT, SNOWY,
        SCULK,
        SANDY,
        RED_SANDY,
        MUDDY,
        CHORUS,
        CHERRY;
    }
}
