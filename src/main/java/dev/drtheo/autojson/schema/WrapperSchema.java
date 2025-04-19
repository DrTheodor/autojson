package dev.drtheo.autojson.schema;

/**
 * One of the base implementations of the {@link Schema} class.
 * A schema used to serialize a class that wraps another object with its own schema.
 *
 * @param <T> the type this schema serializes.
 */
public non-sealed interface WrapperSchema<T> extends Schema<T> {

    @Override
    default Type type() {
        return child().type();
    }

    /**
     * @return the child schema this wrapper schema wraps around.
     */
    Schema<T> child();
}
