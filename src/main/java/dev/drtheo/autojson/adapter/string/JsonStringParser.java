package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.parser.JsonReader;
import dev.drtheo.autojson.schema.base.*;
import org.jetbrains.annotations.NotNull;

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

    @SuppressWarnings("unchecked")
    public static <T> T process(JsonStringAdapter adapter, String raw, Type type) {
        JsonStringParser parser = new JsonStringParser(adapter, raw);
        Schema<T> schema = adapter.schema(type);

        if (schema.type() == SchemaType.OBJECT)
            return parser.deserializeObject(schema.asObject());

        if (schema.type() == SchemaType.ARRAY)
            return (T) parser.deserializeArray(schema.asArray());

        if (AutoJSON.isBuiltIn(type)) {
            if (schema.type() == SchemaType.PRIMITIVE)
                return parser.deserializePrimitive(schema.asPrimitive());

            return parser.deserializePrimitive();
        }

        throw new IllegalArgumentException("No schema for class: " + type.getTypeName());
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

    private <T> T deserializePrimitive() {
        this.setValue(reader.nextToken());
        return decodeBuiltIn();
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
    public <T> Schema<T> schema(Type type) {
        return adapter.schema(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeBuiltIn() {
        return (T) current;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeCustom(Type type, @NotNull Schema<T> fieldSchema) {
        if (this.current == OBJ_MARKER && fieldSchema.type() == SchemaType.OBJECT)
            return deserializeObject(fieldSchema.asObject());

        if (this.current == ARRAY_MARKER && fieldSchema.type() == SchemaType.ARRAY)
            return (T) deserializeArray(reader.peekToken(), fieldSchema.asArray());

        if (fieldSchema.type() == SchemaType.PRIMITIVE)
            return deserializePrimitive(fieldSchema.asPrimitive());

        throw new IllegalStateException("No schema for class " + type.getTypeName());
    }
}
