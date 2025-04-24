package dev.drtheo.autojson.adapter.string.parser2;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.string.JsonStringAdapter;

import java.lang.reflect.Type;

public class JsonStringAdapter2 extends JsonStringAdapter {
    public JsonStringAdapter2(AutoJSON auto) {
        super(auto);
    }

    @Override
    public <R> R fromJson(String object, Type clazz) {
        JsonStringParser2 parser2 = new JsonStringParser2(this, object);
        return parser2.decode(clazz);
    }
}
