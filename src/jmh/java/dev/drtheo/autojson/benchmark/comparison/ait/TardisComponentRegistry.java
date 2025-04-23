package dev.drtheo.autojson.benchmark.comparison.ait;

import dev.drtheo.autojson.benchmark.comparison.ait.data.TardisComponent;

public class TardisComponentRegistry {

    public static TardisComponentRegistry INSTANCE = new TardisComponentRegistry();

    final TardisComponent.IdLike[] ids = new TardisComponent.IdLike[30];

    public TardisComponentRegistry() {
        ids[0] = TardisComponent.IdLike.BIOME;

        ids[2] = TardisComponent.IdLike.CLOAK;
        ids[3] = TardisComponent.IdLike.DOOR;
        ids[4] = TardisComponent.IdLike.ENVIRONMENT;

        ids[6] = TardisComponent.IdLike.FUEL;

        ids[8] = TardisComponent.IdLike.INTERIOR_CHANGING;
        ids[9] = TardisComponent.IdLike.LANDING_PAD;

        ids[11] = TardisComponent.IdLike.OPINION;

        ids[13] = TardisComponent.IdLike.FLIGHT;

        ids[15] = TardisComponent.IdLike.SELF_DESTRUCT;
        ids[16] = TardisComponent.IdLike.ALARMS;
        ids[17] = TardisComponent.IdLike.HUM;
        ids[18] = TardisComponent.IdLike.SHIELDS;
        ids[19] = TardisComponent.IdLike.SIEGE;

        ids[21] = TardisComponent.IdLike.STATS;

        ids[23] = TardisComponent.IdLike.CRASH;
        ids[24] = TardisComponent.IdLike.WAYPOINT;

        for (int i = 0; i < ids.length; i++) {
            TardisComponent.IdLike id = ids[i];

            if (id == null)
                continue;

            id.setIndex(i);
        }
    }

    public TardisComponent.IdLike get(int id) {
        return ids[id];
    }

    public TardisComponent.IdLike get(String key) {
        return TardisComponent.IdLike.valueOf(key);
    }

    public static TardisComponentRegistry getInstance() {
        return INSTANCE;
    }
}
