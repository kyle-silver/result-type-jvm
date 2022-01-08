package dev.kylesilver.result;

@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {
    T get() throws E;
}
