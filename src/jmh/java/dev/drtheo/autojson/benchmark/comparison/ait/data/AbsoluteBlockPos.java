package dev.drtheo.autojson.benchmark.comparison.ait.data;

import java.util.UUID;

public class AbsoluteBlockPos {
    BlockPos pos = new BlockPos(0, 0, 0);
    WorldKey world = new WorldKey(new Id("ait-tardis", UUID.randomUUID().toString()));
}
