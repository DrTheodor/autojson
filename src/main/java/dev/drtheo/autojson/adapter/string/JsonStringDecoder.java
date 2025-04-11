package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.Schema;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.parser.JsonReader;

import java.util.ArrayList;
import java.util.List;

public class JsonStringDecoder implements JsonDeserializationContext {

    private static final Object OBJ_MARKER = new Object();

    private final JsonStringAdapter adapter;
    private final JsonReader reader;

    private Object current;

    public JsonStringDecoder(JsonStringAdapter adapter, String raw) {
        this.adapter = adapter;
        this.reader = new JsonReader(raw);
    }

    public static <T> T process(JsonStringAdapter adapter, String raw, Class<T> clazz) {
        return new JsonStringDecoder(adapter, raw).deserializeObject(adapter.schema(clazz));
    }

    private <T> T deserializeObject(Schema<T> o) {
        T t = o.instantiate();

        while (reader.hasNext()) {
            JsonReader.Token name = reader.nextToken();

            if (name.type() == JsonReader.TokenType.END_OBJECT)
                break;

            if (name.type() != JsonReader.TokenType.NAME)
                continue;

            JsonReader.Token value = reader.nextToken();
            deserializeField(t, (String) name.value(), value, o, reader);
        }

        return t;
    }

    private <T> void deserializeField(T t, String name, JsonReader.Token value, Schema<T> o, JsonReader reader) {
        this.current = switch (value.type()) {
            case BEGIN_ARRAY -> deserializeList(reader);
            case NUMBER, STRING, BOOL, NULL -> value.value();

            case BEGIN_OBJECT -> OBJ_MARKER;

            default -> throw new IllegalStateException(value.type().toString());
        };

        o.deserialize(this.adapter, this, t, name);
    }

    private static List<Object> deserializeList(JsonReader reader) {
        List<Object> result = new ArrayList<>();

        while (reader.hasNext()) {
            JsonReader.Token t = reader.nextToken();

            if (t.type() == JsonReader.TokenType.END_ARRAY)
                return result;

            result.add(t.value());
        }

        return result;
    }

    @Override
    public AutoJSON auto() {
        return this.adapter.auto;
    }

    @Override
    public <T> T decode(Class<T> clazz) {
        if (this.current == OBJ_MARKER)
            return deserializeObject(this.adapter.schema(clazz));

        return (T) this.current;
    }
}
