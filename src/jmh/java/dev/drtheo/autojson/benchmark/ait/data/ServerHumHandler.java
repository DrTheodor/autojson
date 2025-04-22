package dev.drtheo.autojson.benchmark.ait.data;

public class ServerHumHandler extends TardisComponent {
    Id current = new Id("ait", "toyota_hum");

    public ServerHumHandler() {
        super(IdLike.HUM);
    }
}
