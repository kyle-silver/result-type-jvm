package dev.kylesilver.result;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<T, E> {

    static <T, E> Ok<T, E> ok(@NotNull T t) {
        return new Ok<>(t);
    }

    static <T, E> Err<T, E> err(@NotNull E e) {
        return new Err<>(e);
    }

    boolean isOk();

    boolean isErr();

    Optional<T> ok();

    Optional<E> err();

    T unwrap() throws UnwrapException;

    E unwrapErr() throws UnwrapException;

    /**
     * Return the value if Ok, otherwise throw an exception with a custom error
     * message.
     * @param errorMessage
     *      The message to accompany the <code>UnwrapException</code> if thrown.
     * @return
     *      The value, if Ok.
     * @throws UnwrapException
     *      If the result is an Err.
     */
    T expect(String errorMessage) throws UnwrapException;

    /**
     * Returns the value or throws a caller-defined exception.
     * @param mapping
     *      Lambda for creating the thrown exception.
     * @param <F>
     *      Type of the thrown exception.
     * @return
     *      The value, if Ok.
     * @throws F
     *      Thrown if the result is an Err.
     */
    <F extends Throwable> T expect(Function<E, F> mapping) throws F;

    /**
     * Return the value if Err, otherwise throw an exception with a custom error
     * message.
     * @param errorMessage
     *      The message to accompany the <code>UnwrapException</code> if thrown.
     * @return
     *      The value, if Err.
     * @throws UnwrapException
     *      If the result is Ok.
     */
    E expectErr(String errorMessage) throws UnwrapException;

    /**
     * Returns the value or throws a caller-defined exception.
     * @param mapping
     *      Lambda for creating the thrown exception.
     * @param <F>
     *      Type of the thrown exception.
     * @return
     *      The value, if Err.
     * @throws F
     *      Thrown if the result is Ok.
     */
    <F extends Throwable> E expectErr(Function<T, F> mapping) throws F;

    <V> V match(Function<T, V> ifOk, Function<E, V> ifErr);

    void match(Consumer<T> ifOk, Consumer<E> ifErr);

    <V> Result<V, E> map(Function<T, V> mapping);

    <F> Result<T, F> mapErr(Function<E, F>  mapping);
}
