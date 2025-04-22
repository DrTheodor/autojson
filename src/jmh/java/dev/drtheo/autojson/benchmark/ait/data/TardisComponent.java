package dev.drtheo.autojson.benchmark.ait.data;

import java.util.function.Supplier;

public class TardisComponent {

    private final IdLike id;

    public TardisComponent(IdLike id) {
        this.id = id;
    }

    public IdLike getId() {
        return id;
    }

    public enum IdLike {
        BIOME(BiomeHandler.class, BiomeHandler::new),
        CLOAK(CloakHandler.class, CloakHandler::new),
        DOOR(DoorHandler.class, DoorHandler::new),
        ENVIRONMENT(ExteriorEnvironmentHandler.class, ExteriorEnvironmentHandler::new),
        FUEL(FuelHandler.class, FuelHandler::new),
        INTERIOR_CHANGING(InteriorChangingHandler.class, InteriorChangingHandler::new),
        LANDING_PAD(LandingPadHandler.class, LandingPadHandler::new),
        OPINION(OpinionHandler.class, OpinionHandler::new),
        FLIGHT(RealFlightHandler.class, RealFlightHandler::new),
        SELF_DESTRUCT(SelfDestructHandler.class, SelfDestructHandler::new),
        ALARMS(ServerAlarmHandler.class, ServerAlarmHandler::new),
        HUM(ServerHumHandler.class, ServerHumHandler::new),
        SHIELDS(ShieldHandler.class, ShieldHandler::new),
        SIEGE(SiegeHandler.class, SiegeHandler::new),
        STATS(StatsHandler.class, StatsHandler::new),
        CRASH(CrashHandler.class, CrashHandler::new),
        WAYPOINT(WaypointHandler.class, WaypointHandler::new);

        private final Class<?> clazz;
        private final Supplier<TardisComponent> supplier;
        private int index;

        IdLike(Class<?> clazz, Supplier<TardisComponent> supplier) {
            this.clazz = clazz;
            this.supplier = supplier;
        }

        public Class<?> clazz() {
            return clazz;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public TardisComponent create() {
            return supplier.get();
        }
    }
}
