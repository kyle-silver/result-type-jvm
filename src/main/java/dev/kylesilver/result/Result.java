package dev.kylesilver.result;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A result type represents the output of an operation with two possible
 * outcomes. The operation can either yield an {@link Ok}, in which case it was
 * performed successfully and without any errors, or it can be an {@link Err},
 * meaning that something unexpected or invalid occurred. This could be anything
 * from a user supplying an incorrect value to a network connection closing
 * unexpectedly.
 * <p>
 * Normally JVM languages would handle these conditions with exceptions, but
 * doing so can lead to control flow which is difficult to keep track of. With
 * a result type, all conditions are covered by virtue of being correctly typed.
 * @param <T>
 * @param <E>
 */
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

    /**
     * Indicates whether the wrapped value is an {@link Ok}.
     * @return <code>true</code> if the wrapper is {@link Ok}, otherwise <code>
     *      false</code>.
     */
    boolean isOk();

    /**
     * Indicates whether the wrapped value is an {@link Err}.
     * @return <code>true</code> if the wrapper is {@link Err}, otherwise <code>
     *      false</code>.
     */
    boolean isErr();

    /**
     * Get the {@link Ok} value if it exists.
     * @return the underlying value if the result is {@link Ok}, otherwise the
     *      value will be empty.
     * @see Optional
     */
    Optional<T> ok();

    /**
     * Get the {@link Err} value if it exists.
     * @return the underlying value if the result is an {@link Err}, otherwise
     *      the value will be empty.
     * @see Optional
     */
    Optional<E> err();

    /**
     * Retrieve the {@link Ok} value or throw an exception if the result is an
     * {@link Err}.
     * @return
     *      the value of {@link Ok} without an {@link Optional} wrapper.
     * @throws UnwrapException
     *      if the result is not {@link Ok}.
     */
    T unwrap() throws UnwrapException;

    /**
     * Retrieve the {@link Err} value or throw an {@link UnwrapException} if the
     * result is {@link Ok}. If the error is a {@link Throwable}, it will
     * <strong>not</strong> be thrown. To throw a custom error, use
     * {@link #expect(Function)}.
     * @return
     *      the value of {@link Err} without an {@link Optional} wrapper.
     * @throws UnwrapException
     *      if the result is not {@link Err}.
     */
    E unwrapErr() throws UnwrapException;

    /**
     * Return the value if {@link Ok}, otherwise throw an exception with a
     * custom error message.
     * @param errorMessage
     *      The message to accompany the {@link UnwrapException} if thrown.
     * @return
     *      The value, if {@link Ok}.
     * @throws UnwrapException
     *      If the result is an {@link Err}.
     */
    T expect(String errorMessage) throws UnwrapException;

    /**
     * Returns the value or throws a caller-defined exception.
     * @param mapping
     *      Lambda for creating the thrown exception.
     * @param <F>
     *      Type of the thrown exception.
     * @return
     *      The value, if {@link Ok}.
     * @throws F
     *      Thrown if the result is an {@link Err}.
     */
    <F extends Throwable> T expect(Function<E, F> mapping) throws F;

    /**
     * Return the value if {@link Err}, otherwise throw an exception with a
     * custom error message.
     * @param errorMessage
     *      The message to accompany the {@link UnwrapException} if thrown.
     * @return
     *      The value, if {@link Err}.
     * @throws UnwrapException
     *      If the result is {@link Ok}.
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

    /**
     * Apply a transformation to the value of a result.
     * <p>
     * In the example below, the <code>parseArgs</code> function attempts to
     * convert a user-provided string to a list of integers, but failing that
     * returns its input unmodified. The output of the function is then used to
     * determine the number of valid user-provided arguments and do some
     * additional logging for convenience.
     * <pre>{@code
     * Result<List<Integer>, String> parsed = parseArgs(input);
     * int validArgs = parsed.match(
     *     ok -> ok.size(),
     *     err -> {
     *         log.warn("The provided input could not be parsed: \"{}\"", err);
     *         return 0;
     *     }
     * );
     * }</pre>
     * Note that unlike {@link #map(Function)}, both the {@link Ok} and
     * {@link Err} values must be mapped to a single output type.
     * @param ifOk
     *      the transformation that is applied to the underlying data if the
     *      result is {@link Ok}.
     * @param ifErr
     *      the transformation that is applied to the underlying data if the
     *      result is an {@link Err}.
     * @param <U>
     *      the type of the value to be returned after the transformations are
     *      applied. Both transformations must converge to this type.
     * @return
     *      a value of type <code>U</code> after applying one of the two
     *      provided transformations.
     */
    <U> U match(Function<T, U> ifOk, Function<E, U> ifErr);

    /**
     * Apply a lambda with no return type to the value of a result.
     * <p>
     * This is useful for things which produce side effects such as logging. In
     * contrast to {@link #match(Function, Function)}, no output can be obtained
     * from an invocation of this method. In the example below, some
     * informational logging is performed on the <code>parseArgs</code>
     * function.
     * <pre>{@code
     * Result<List<Integer>, String> parsed = parseArgs(input);
     * parsed.match(
     *     ok -> log.info("args were parsed successfully: {}", ok),
     *     err -> log.warn("The provided input could not be parsed: \"{}\"", err)
     * );
     * }</pre>
     * @param ifOk
     *      the lambda that is applied to the underlying data if the result is
     *      {@link Ok}.
     * @param ifErr
     *      the lambda that is applied to the underlying data if the result is
     *      an {@link Err}.
     */
    void match(Consumer<T> ifOk, Consumer<E> ifErr);

    /**
     * Apply a transformation to the wrapped value if the result is {@link Ok}.
     * If the result is an {@link Err}, no transformation will be applied.
     * <pre>{@code
     * assertEquals(
     *     6,
     *     Result.ok(5).map(x -> x + 1).unwrap()
     * );
     * assertEquals(
     *     "error",
     *     Result.<Integer, String>err("error").map(x -> x + 1).unwrapErr()
     * );
     * }</pre>
     * If an exception is thrown while applying {@code mapping}, <strong>the
     * exception will not be caught</strong>. Ensure that any lambda passed to
     * this function does not throw any runtime exceptions.
     * @param mapping
     *      the transformation to apply to the wrapped value if the result is
     *      {@link Ok}.
     * @param <U>
     *      the type of the output of the transformation.
     * @return
     *      a new {@link Result} containing either the transformed value or the
     *      original error.
     */
    <U> Result<U, E> map(Function<T, U> mapping);

    /**
     * Apply a transformation to the wrapped value if the result is an
     * {@link Err}. If the result is {@link Ok}, no transformation will be
     * applied.
     * <pre>{@code
     * assertEquals(
     *     "error: 'foo'",
     *      Result.err("foo").mapErr(err -> String.format("error: '%s'", err)).unwrapErr()
     * );
     * assertEquals(
     *     5,
     *     Result.ok(5).mapErr(err -> String.format("error: '%s'", err)).unwrap()
     * );
     * }</pre>
     * If an exception is thrown while applying {@code mapping}, <strong>the
     * exception will not be caught</strong>. Ensure that any lambda passed to
     * this function does not throw any runtime exceptions.
     * @param mapping
     *      the transformation to apply to the wrapped value if the result is an
     *      {@link Err}.
     * @param <F>
     *      the type of the output of the transformation.
     * @return
     *      a new {@link Result} containing either the transformed error or the
     *      original value.
     */
    <F> Result<T, F> mapErr(Function<E, F>  mapping);

    /**
     * Returns the provided argument if the result is {@link Ok} and propagates
     * the original {@link Err} otherwise.
     * <pre>{@code
     * assertEquals(
     *     Result.err("first error"),
     *     Result.err("first error").and(Result.err("second error"))
     * );
     * assertEquals(
     *     Result.err("second error"),
     *     Result.ok(1).and(Result.err("second error"))
     * );
     * assertEquals(
     *     Result.ok(2),
     *     Result.ok("success").and(Result.ok(2))
     * );
     * }</pre>
     * @param result
     *      the value to be returned if the result is {@link Ok}.
     * @param <U>
     *      the type of the {@link Ok} value in the new result.
     * @return
     *      either the original error or the caller-provided {@code Result}.
     */
    <U> Result<U, E> and(Result<U, E> result);

    /**
     * Apply a fallible operation to the wrapped value if the result is
     * {@link Ok}. If the result is an {@link Err}, the error is propagated to
     * the next operation in the chain.
     * <p>
     * This operation differs from {@link #map(Function) map} in that if the
     * value yielded by the provided fallible operation is an error, it is
     * returned without being wrapped in a redundant, enclosing {@link Result}.
     * Take, for example, two functions: {@code getUserInput()} which returns a
     * {@code Result<String, RuntimeException>} and
     * {@code parseInt(String s)} which returns a
     * {@code Result<Integer, NumberFormatException>}. A chain of operations
     * could be built up as follows:
     * <pre>{@code
     * Result<Integer, RuntimeException> result = getUserInput().andThen(parseInt);
     * }</pre>
     * If {@link #map(Function) map} had been used instead, the chain would have
     * looked like:
     * <pre>{@code
     * Result<Result<Integer, NumberFormatException>, NoSuchElementException> result = getUserInput().map(parseInt);
     * }</pre>
     * @param resultFn
     *      the function to be applied if the result is {@link Ok}.
     * @param <U>
     *      the output type of {@code resultFn}.
     * @return
     *      either the output of {@code resultFn} or the original error.
     */
    <U> Result<U, E> andThen(Function<T, Result<U, E>> resultFn);

    /**
     * Returns the provided argument if the result is an {@link Err} and
     * propagates the original {@link Ok} value otherwise.
     * <pre>{@code
     * assertEquals(
     *     Result.ok("success"),
     *     Result.err("error").or(Result.ok("success"))
     * );
     * assertEquals(
     *     Result.ok("first"),
     *     Result.ok("first").or(Result.ok("second"))
     * );
     * assertEquals(
     *     Result.err("second"),
     *     Result.err("first").or(Result.err("second"))
     * );
     * }</pre>
     * @param result
     *      the value to be returned if the result is an {@link Err}.
     * @param <F>
     *      the type of the {@link Err} value in the new result.
     * @return
     *      either the original value or the caller-provided {@code Result}.
     */
    <F> Result<T, F> or(Result<T, F> result);

    /**
     * Apply a fallible operation to the wrapped value if the result is an
     * {@link Err}. If the result is {@link Ok}, the value is propagated to the
     * next operation in the chain.
     * <pre>{@code
     * assertEquals(
     *     Result.ok("recovered from err: foo"),
     *     Result.err("foo").orElse(err -> Result.ok("recovered from err: " + err))
     * );
     * assertEquals(
     *     Result.ok("no error"),
     *     Result.ok("no error").orElse(err -> Result.ok("recovered from err: " + err))
     * );
     * }</pre>
     * @param resultFn
     *      the function to be applied if the result is an {@link Err}.
     * @param <F>
     *      the type of the {@link Err} value in the new result.
     * @return
     *      either the output of {@code resultFn} or the original value.
     */
    <F> Result<T, F> orElse(Function<E, Result<T, F>> resultFn);
}
