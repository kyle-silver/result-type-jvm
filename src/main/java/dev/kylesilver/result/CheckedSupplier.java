package dev.kylesilver.result;

import java.util.function.Supplier;

/**
 * Similar to the {@link java.util.function.Supplier} interface, but with
 * additional type information to allow for the function to throw an exception.
 * @param <T>
 *      The type of the value yielded by the function
 * @param <E>
 *      The type of an exception thrown by the function. Since there is no
 *      mechanism in the {@link Result} class for catching multiple exception
 *      types, {@code <E>} must be a parent type to all exceptions that the
 *      function might throw. If this condition is not met, a {@link
 *      ErrorTypeMismatchException} will be thrown at runtime.
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {
    /**
     * @see Supplier#get()
     */
    T get() throws E;
}
