package dev.kylesilver.result;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<T, E> {

    /**
     * An <code>Ok</code> indicates that a fallible operation has been performed
     * successfully and that the caller has received their desired result.
     * @param t
     *      the output value of the fallible operation.
     * @param <T>
     *      the type of <code>T</code>.
     * @param <E>
     *      the type of any errors that the fallible operation might produce. In
     *      the <code>Ok</code> case this type is unused, but knowing it is
     *      important for building robust error handling.
     * @return
     *      an <code>Ok</code> result wrapping <code>t</code>.
     */
    static <T, E> Ok<T, E> ok(@NotNull T t) {
        return new Ok<>(t);
    }

    /**
     * An <code>Err</code> indicates that a fallible operation has failed and
     * yielded an error value.
     * @param e
     *      the value of the error. This can be any object and does not need to
     *      be a subclass of <code>Throwable</code>. If an exception needs to be
     *      thrown on an error, use the {@link #unwrap() unwrap} or {@link
     *      #expect(Function) expect} methods.
     * @param <T>
     *      the type of the value that the fallible operation provides on
     *      success. In the <code>Err</code> case this type is unused, but
     *      knowing it is important for building robust error handling.
     * @param <E>
     *      the type of <code>e</code>.
     * @return
     *      an <code>Err</code> result wrapping <code>e</code>.
     */
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

    <U> U match(Function<T, U> ifOk, Function<E, U> ifErr);

    void match(Consumer<T> ifOk, Consumer<E> ifErr);

    <U> Result<U, E> map(Function<T, U> mapping);

    <F> Result<T, F> mapErr(Function<E, F>  mapping);

    default <U> Result<U, E> and(Result<U, E> result) {
        return this.match(ok -> result, Result::err);
    }

    default <U> Result<U, E> andThen(Function<T, Result<U, E>> resultFn) {
        return this.match(resultFn, Result::err);
    }

    default <F> Result<T, F> or(Result<T, F> result) {
        return this.match(Result::ok, err -> result);
    }

    default <F> Result<T, F> orElse(Function<E, Result<T, F>> resultFn) {
        return this.match(Result::ok, resultFn);
    }
}
