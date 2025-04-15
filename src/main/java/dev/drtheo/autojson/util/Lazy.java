package dev.drtheo.autojson.util;

import java.util.function.Supplier;

public class Lazy<T> {

    private final Supplier<T> supplier;
    public T value;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.value == null)
            this.value = this.supplier.get();

        return value;
    }
}
