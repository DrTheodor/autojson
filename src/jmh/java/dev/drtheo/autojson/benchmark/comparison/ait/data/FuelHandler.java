package dev.drtheo.autojson.benchmark.comparison.ait.data;

public class FuelHandler extends TardisComponent {

    double fuel = 50000;
    boolean refueling;
    boolean power;

    public FuelHandler() {
        super(IdLike.FUEL);
    }
}
