package dev.drtheo.autojson.benchmark.ait;

import java.util.HashSet;

public class StatsHandler {
    String name;
    String creator;
    String creationDate;
    String dateTimezone;
    WorldKey skybox = new WorldKey(new Id("ait", "space"));
    HashSet<String> unlocks = new HashSet<>() {{
        add("ait:toyota");
        add("ait:coral");
    }};

    boolean security;
    boolean hailMary;
    boolean receiveCalls;
    Id dematId = new Id("ait", "default");
    Id matId = new Id("ait", "default");
    Id flightId = new Id("ait", "default");
    Id vortexId = new Id("ait", "peanut");

    double xScale;
    double yScale;
    double zScale;
}
