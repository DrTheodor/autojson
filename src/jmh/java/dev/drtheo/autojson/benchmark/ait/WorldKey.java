package dev.drtheo.autojson.benchmark.ait;

public class WorldKey {
    public Id path;
    public Id registry = new Id("minecraft", "world");

    public WorldKey(Id path) {
        this.path = path;
    }
}
