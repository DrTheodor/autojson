package dev.drtheo.autojson.benchmark.ait.data;

import java.util.UUID;

public class SiegeHandler extends TardisComponent {
    boolean active;
    Id texture = new Id("ait", "textures/blockentities/exteriors/siege_mode/siege_mode.png");
    UUID held;
    int siegeTime;

    public SiegeHandler() {
        super(IdLike.SIEGE);
    }
}
