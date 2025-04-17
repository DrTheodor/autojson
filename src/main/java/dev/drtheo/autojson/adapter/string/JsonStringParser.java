package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.schema.ArraySchema;
import dev.drtheo.autojson.schema.ObjectSchema;
import dev.drtheo.autojson.schema.PrimitiveSchema;
import dev.drtheo.autojson.schema.Schema;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.parser.JsonReader;

import java.lang.reflect.Type;

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

    public static <T> T process(JsonStringAdapter adapter, String raw, Type type) {
        JsonStringParser parser = new JsonStringParser(adapter, raw);
        Schema<T> schema = adapter.schema(type);

        if (schema instanceof ObjectSchema<T> obj)
            return parser.deserializeObject(obj);

        if (schema instanceof ArraySchema<T, ?> arr)
            return (T) parser.deserializeArray(arr);

        if (type == null || AutoJSON.isPrimitive(type)) {
            if (schema instanceof PrimitiveSchema<T> ps)
                return parser.deserializePrimitive(ps);

            return parser.deserializePrimitive(type);
        }

        throw new IllegalArgumentException("Unsupported schema type: " + type);
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

    private <T> Object deserializeArray(ArraySchema<T, ?> o) {
        return deserializeArray(reader.nextToken(), o);
    }

    private <T, Intr> Object deserializeArray(JsonReader.Token start, ArraySchema<T, Intr> o) {
        if (start.type() != JsonReader.TokenType.BEGIN_ARRAY)
            throw new IllegalStateException("Not an array");

        Intr t = o.instantiate();
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

    private <T> T deserializePrimitive(Type type) {
        this.setValue(reader.nextToken());
        return decode(type);
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
    public <T> T decode(Type type) {
        return decode(type, this.adapter.schema(type));
    }

    @Override
    public <T> T decode(Type type, Schema<T> fieldSchema) {
        if (this.current == OBJ_MARKER && fieldSchema instanceof ObjectSchema<T> objSchema)
            return deserializeObject(objSchema);

        if (this.current == ARRAY_MARKER && fieldSchema instanceof ArraySchema<T, ?> arraySchema)
            return (T) deserializeArray(reader.peekToken(), arraySchema);

        if (type == null || AutoJSON.isPrimitive(type)) {
            if (fieldSchema instanceof PrimitiveSchema<T> ps)
                return deserializePrimitive(ps);

            return (T) current;
        }

        throw new IllegalStateException("No schema for class " + type.getTypeName());
    }
}
