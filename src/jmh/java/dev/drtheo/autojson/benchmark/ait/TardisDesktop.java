package dev.drtheo.autojson.benchmark.ait;

import java.util.HashSet;
import java.util.Set;

public class TardisDesktop {
    TardisDesktopSchema schema = new TardisDesktopSchema();
    AbsoluteBlockPos doorPos = new AbsoluteBlockPos();
    Corners corners = new Corners();
    Set<BlockPos> consolePos = new HashSet<>();

    {
        consolePos.add(new BlockPos(0, 100, 0));
    }
}
