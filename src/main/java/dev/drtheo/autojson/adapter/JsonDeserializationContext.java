package dev.drtheo.autojson.adapter;

public interface JsonDeserializationContext extends JsonContext {
    <T> T decode(Class<T> clazz);
}
