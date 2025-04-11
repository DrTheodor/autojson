package dev.drtheo.autojson;

import dev.drtheo.autojson.adapter.JsonAdapter;
import dev.drtheo.autojson.bake.unsafe.BakedAutoSchema;

import java.util.HashMap;
import java.util.Map;

public class AutoJSON {

    private final Map<Class<?>, BakedAutoSchema<?>> schemas = new HashMap<>();

    private int layer = 0;
    private boolean logMisingEntries = true;

    public void setLogMisingEntries(boolean logMisingEntries) {
        this.logMisingEntries = logMisingEntries;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void bake(Class<?> clazz) {
        this.schemas.put(clazz, BakedAutoSchema.bake(clazz));
    }

    public <T> Schema<T> schema(Class<?> clazz) {
        return (Schema<T>) schemas.computeIfAbsent(clazz, BakedAutoSchema::bake);
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj) {
        return toJson(adapter, obj, obj.getClass());
    }

    public <F, T> T toJson(JsonAdapter<F, T> adapter, Object obj, Class<?> clazz) {
        return adapter.toJson(obj, clazz);
    }

    public <F, T> F fromJson(JsonAdapter<F, T> adapter, T object, Class<F> clazz) {
        return adapter.fromJson(object, clazz);
    }

    public int layer() {
        return layer;
    }

    public boolean logMissingEntries() {
        return true;
    }

    public void log(String message) {
        System.out.println("[INFO] " + message);
    }

    public void warn(String message) {
        System.err.println("[WARN] " + message);
    }
}
