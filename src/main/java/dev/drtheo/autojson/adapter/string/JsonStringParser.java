package dev.drtheo.autojson.adapter.string;

import dev.drtheo.autojson.AutoJSON;
import dev.drtheo.autojson.adapter.JsonDeserializationContext;
import dev.drtheo.autojson.adapter.string.parser.JsonReader;
import dev.drtheo.autojson.adapter.string.parser.Token;
import dev.drtheo.autojson.adapter.string.parser.TokenType;
import dev.drtheo.autojson.schema.base.*;
import dev.drtheo.autojson.util.ClassAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import static dev.drtheo.autojson.adapter.string.parser.TokenType.*;

public class JsonStringParser implements JsonDeserializationContext {

    private static final Object OBJ_MARKER = new Object();
    private static final Object ARRAY_MARKER = new Object();
    private static final Object NONE = new Object();

    private final JsonStringAdapter adapter;
    private final JsonReader reader;

    private Object current = NONE;

    public JsonStringParser(JsonStringAdapter adapter, String raw) {
        this.adapter = adapter;
        this.reader = new JsonReader(raw);
    }

    public static <T> T process(JsonStringAdapter adapter, String raw, Type type) {
        JsonStringParser parser = new JsonStringParser(adapter, raw);
        Schema<T> schema = adapter.schema(type);

        return parser.processSchema(schema, type);
    }

    @SuppressWarnings("unchecked")
    private <T> T processSchema(Schema<T> schema, Type type) {
        if (schema == null) {
            if (AutoJSON.isBuiltIn(type)) {
                this.setValue(reader.nextToken());

                T t = this.decodeBuiltIn();

                if (ClassAdapter.matchUnboxed(type) instanceof ClassAdapter.Wrapped<?,?,?> wrapped)
                    return (T) unwrap(t, wrapped);

                return t;
            }

            throw new IllegalArgumentException("No schema for class: " + type.getTypeName());
        }

        if (schema.type() == SchemaType.OBJECT)
            return this.deserializeObject(schema.asObject());

        if (schema.type() == SchemaType.ARRAY)
            return (T) this.deserializeArray(schema.asArray());

        if (schema.type() == SchemaType.WRAPPER)
            return this.deserializeWrapper(schema.asWrapper());

        if (schema.type() == SchemaType.PRIMITIVE)
            return this.deserializePrimitive(schema.asPrimitive());

        throw new IllegalStateException("Unreachable");
    }

    @SuppressWarnings("unchecked")
    private static <T, B> T unwrap(Object value, ClassAdapter.Wrapped<T, ?, B> wrapped) {
        return wrapped.unwrap((B) value);
    }

    private <T, B> T deserializeWrapper(WrapperSchema<T, B> wrapper) {
        B b = this.decode(wrapper.wrapping(), wrapper.child());
        return wrapper.deserialize(this.adapter, b);
    }

    private <T> T deserializeObject(ObjectSchema<T> o) {
        current = NONE;
        T t = o.instantiate();

        while (reader.hasNext()) {
            Token name = reader.nextToken();

            if (name.type == TokenType.END_OBJECT)
                break;

            if (name.type != TokenType.NAME)
                continue;

            Token value = reader.nextToken();

            this.setValue(value);
            o.deserialize(this.adapter, this, t, (String) name.value);
        }

        return t;
    }

    private <T, Intr> Object deserializeArray(ArraySchema<T, Intr> o) {
        if (current != ARRAY_MARKER)
            reader.nextToken();
        current = NONE;

        Intr t = o.instantiate();
        int i = 0;

        while (reader.hasNext()) {
            Token value = reader.nextToken();

            if (value.type == TokenType.END_ARRAY)
                break;

            this.setValue(value);
            t = o.deserialize(this.adapter, this, t, i++);
        }

        return o.pack(t);
    }

    private <T> T deserializePrimitive(PrimitiveSchema<T> o) {
        if (this.current == NONE)
            this.setValue(reader.nextToken());

        return o.deserialize(this.adapter, this);
    }

    private void setValue(Token value) {
        this.current = switch (value.type) {
            case NUMBER, STRING, BOOL, NULL -> value.value;

            case BEGIN_ARRAY -> ARRAY_MARKER;
            case BEGIN_OBJECT -> OBJ_MARKER;

            default -> throw new IllegalStateException(TokenType.from(value.type));
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
        Object o = current;
        current = NONE;

        return (T) o;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeCustom(Type type, @NotNull Schema<T> fieldSchema) {
        if (this.current == OBJ_MARKER && fieldSchema.type() == SchemaType.OBJECT)
            return deserializeObject(fieldSchema.asObject());

        if (this.current == ARRAY_MARKER && fieldSchema.type() == SchemaType.ARRAY)
            return (T) deserializeArray(fieldSchema.asArray());

        if (fieldSchema.type() == SchemaType.PRIMITIVE)
            return deserializePrimitive(fieldSchema.asPrimitive());

        if (fieldSchema.type() == SchemaType.WRAPPER)
            return deserializeWrapper(fieldSchema.asWrapper());

        throw new IllegalStateException("No schema for class " + type.getTypeName());
    }
}
