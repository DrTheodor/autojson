package dev.drtheo.autojson.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class FastStringMap<V> implements Map<String, V> {

    private final Object[] values;
    private final int[] hashes;

    private int cursor;

    public FastStringMap(int size) {
        this.values = new Object[size];
        this.hashes = new int[size];
    }

    @Override
    public int size() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isEmpty() {
        throw new IllegalStateException();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new IllegalStateException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new IllegalStateException();
    }

    @Override
    public V get(Object key) {
        throw new IllegalStateException();
    }

    public V put(String key, V value) {
        values[cursor] = value;
        hashes[cursor++] = fastHash(key);
        return null;
    }

    @Override
    public V remove(Object key) {
        throw new IllegalStateException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends V> m) {
        throw new IllegalStateException();
    }

    @Override
    public void clear() {
        throw new IllegalStateException();
    }

    @Override
    public @NotNull Set<String> keySet() {
        throw new IllegalStateException();
    }

    @Override
    public @NotNull Collection<V> values() {
        throw new IllegalStateException();
    }

    @Override
    public @NotNull Set<Entry<String, V>> entrySet() {
        throw new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    public V get(String key) {
        int hash = fastHash(key);
        for (int i = 0; i < hashes.length; i++) {
            if (hash == hashes[i]) {
                return (V) values[i];
            }
        }

        return null;
    }

    private static int fastHash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }
}
