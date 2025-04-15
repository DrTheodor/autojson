package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.PrimitiveSchema;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.parser.JsonReader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JsonStringParser implements JsonDeserializationContext {

    private static final Object OBJ_MARKER = new Object();
    private static final Object ARRAY_MARKER = new Object();

    private final JsonStringAdapter adapter;
    private final JsonReader reader;

    private Object current;

    public JsonStringParser(JsonStringAdapter adapter, String raw) {
        this.adapter = adapter;
        this.reader = new JsonReader(raw);
    }

    public static <T> T process(JsonStringAdapter adapter, String raw, Class<T> clazz) {
        JsonStringParser parser = new JsonStringParser(adapter, raw);
        Schema<T> schema = adapter.schema(clazz);

        if (schema instanceof ObjectSchema<T> obj)
            return parser.deserializeObject(obj);

        if (schema instanceof ArraySchema<T> arr)
            return (T) parser.deserializeArray(arr);

        if (AutoJSON.isPrimitive(clazz)) {
            if (schema instanceof PrimitiveSchema<T> ps)
                return parser.deserializePrimitive(ps);

            return parser.deserializePrimitive(clazz);
        }

        throw new IllegalArgumentException("Unsupported schema type: " + clazz);
    }

    private <T> T deserializeObject(ObjectSchema<T> o) {
        T t = o.instantiate();

        while (reader.hasNext()) {
            JsonReader.Token name = reader.nextToken();

            if (name.type() == JsonReader.TokenType.END_OBJECT)
                break;

            if (name.type() != JsonReader.TokenType.NAME)
                continue;

            JsonReader.Token value = reader.nextToken();

            this.setValue(value);
            o.deserialize(this.adapter, this, t, (String) name.value());
        }

        return t;
    }

    private <T> Object deserializeArray(ArraySchema<T> o) {
        return deserializeArray(reader.nextToken(), o);
    }

    private <T> Object deserializeArray(JsonReader.Token start, ArraySchema<T> o) {
        List<JsonReader.Token> listBuf = new ArrayList<>();

        if (start.type() != JsonReader.TokenType.BEGIN_ARRAY)
            throw new IllegalStateException("Not an array");

        Object t = o.instantiate();
        int i = 0;

        while (reader.hasNext()) {
            JsonReader.Token value = reader.nextToken();

            if (value.type() == JsonReader.TokenType.END_ARRAY)
                break;

            this.setValue(value);
            t = o.deserialize(this.adapter, this, t, i++);
        }

        return o.pack(t);
    }

    private <T> T deserializePrimitive(PrimitiveSchema<T> o) {
        this.setValue(reader.nextToken());
        return o.deserialize(this.adapter, this);
    }

    private <T> T deserializePrimitive(Class<T> c) {
        this.setValue(reader.nextToken());
        return decode(c);
    }

    private void setValue(JsonReader.Token value) {
        this.current = switch (value.type()) {
            case NUMBER, STRING, BOOL, NULL -> value.value();

            case BEGIN_ARRAY -> ARRAY_MARKER;
            case BEGIN_OBJECT -> OBJ_MARKER;

            default -> throw new IllegalStateException(value.type().toString());
        };
    }

    @Override
    public AutoJSON auto() {
        return this.adapter.auto;
    }

    @Override
    public <T> T decode(Class<T> clazz) {
        return decode(clazz, () -> this.adapter.schema(clazz));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(Class<T> clazz, Supplier<Schema<T>> supplier) {
        Schema<T> fieldSchema = supplier.get();

        if (this.current == OBJ_MARKER)
            return deserializeObject((ObjectSchema<T>) fieldSchema);

        if (this.current == ARRAY_MARKER)
            return (T) deserializeArray(reader.peekToken(), (ArraySchema<T>) fieldSchema);

        if (AutoJSON.isPrimitive(clazz)) {
            if (fieldSchema instanceof PrimitiveSchema<T> ps)
                return deserializePrimitive(ps);

            return (T) current;
        }

        throw new IllegalStateException("No schema for class " + clazz);
    }
}
