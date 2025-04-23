package dev.drtheo.autojson.benchmark.comparison.ait.data;

import java.util.ArrayList;
import java.util.List;

public class OpinionHandler extends TardisComponent {
    List<Id> opinions = new ArrayList<>();

    {
        opinions.add(new Id("ait", "zeiton"));
        opinions.add(new Id("minecraft", "dirt"));
    }

    public OpinionHandler() {
        super(IdLike.OPINION);
    }
}
