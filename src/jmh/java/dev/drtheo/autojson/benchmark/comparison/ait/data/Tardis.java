package dev.drtheo.autojson.benchmark.comparison.ait.data;

import java.util.UUID;

public class Tardis {

    UUID id = UUID.randomUUID();
    TardisDesktop desktop = new TardisDesktop();
    TardisExterior exterior = new TardisExterior();
    public TardisHandlersManager handlers = new TardisHandlersManager();
}
