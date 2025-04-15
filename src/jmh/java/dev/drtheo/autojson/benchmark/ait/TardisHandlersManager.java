package dev.drtheo.autojson.benchmark.ait;

public class TardisHandlersManager {

    public Object[] components = new Object[30];

    {
        components[0] = new BiomeHandler();
//            components[1] = new ButlerHandler();
        components[2] = new CloakHandler();
        components[3] = new DoorHandler();
        components[4] = new ExteriorEnvironmentHandler();
//        components[5] = new ExtraHandler();
        components[6] = new FuelHandler();
//        components[7] = new HadsHandler();
        components[8] = new InteriorChangingHandler();
        components[9] = new LandingPadHandler();
//        components[10] = new LoyaltyHandler();
        components[11] = new OpinionHandler();
//        components[12] = new OvergrownHandler();
        components[13] = new RealFlightHandler();
//        components[14] = new SeatHandler();
        components[15] = new SelfDestructHandler();
        components[16] = new ServerAlarmHandler();
        components[17] = new ServerHumHandler();
        components[18] = new ShieldHandler();
        components[19] = new SiegeHandler();
//        components[20] = new SonicHandler();
        components[21] = new StatsHandler();
//        components[22] = new SubSystemHandler();
        components[23] = new CrashHandler();
        components[24] = new WaypointHandler();
    };
}
