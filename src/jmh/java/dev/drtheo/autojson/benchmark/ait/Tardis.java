package dev.drtheo.autojson.benchmark.ait;

import java.util.UUID;

public class Tardis {

    UUID id = UUID.randomUUID();
    TardisDesktop desktop = new TardisDesktop();
    TardisExterior exterior = new TardisExterior();
    TardisHandlersManager handlers = new TardisHandlersManager();
}
