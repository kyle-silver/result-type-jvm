package dev.kylesilver.result;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Indicates that an operation has been completed successfully.
 * @param <T>
 *      The type of the value wrapped by this class.
 * @param <E>
 *      The type of an {@link Err} that could have (but in this case did not)
 *      resulted from the parent operation.
 */
@ToString
@EqualsAndHashCode
public class Ok<T, E> implements Result<T, E> {
    private final T t;

    public Ok(@NotNull T t) {
        this.t = t;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public Optional<T> ok() {
        return Optional.of(t);
    }

    @Override
    public Optional<E> err() {
        return Optional.empty();
    }

    @Override
    public T unwrap() {
        return t;
    }

    @Override
    public E unwrapErr() throws UnwrapException {
        throw new UnwrapException("The Result was expected to be Err, but was instead Ok with value " + t);
    }

    @Override
    public T expect(String errorMessage) {
        return t;
    }

    @Override
    public <F extends Throwable> T expect(Function<E, F> mapping) {
        return t;
    }

    @Override
    public E expectErr(String errorMessage) throws UnwrapException {
        throw new UnwrapException(errorMessage);
    }

    @Override
    public <F extends Throwable> E expectErr(Function<T, F> mapping) throws F {
        throw mapping.apply(t);
    }

    @Override
    public <U> U match(Function<T, U> ifOk, Function<E, U> ifErr) {
        return ifOk.apply(t);
    }

    @Override
    public void match(Consumer<T> ifOk, Consumer<E> ifErr) {
        ifOk.accept(t);
    }

    @Override
    public <U> Result<U, E> map(Function<T, U> mapping) {
        return Result.ok(mapping.apply(t));
    }

    @Override
    public <F> Result<T, F> mapErr(Function<E, F> mapping) {
        return Result.ok(t);
    }

    @Override
    public <U> Result<U, E> and(Result<U, E> result) {
        return result;
    }

    @Override
    public <U> Result<U, E> andThen(Function<T, Result<U, E>> resultFn) {
        return resultFn.apply(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F> Result<T, F> or(Result<T, F> result) {
        // So why is this unchecked cast okay? Well, the generic error type in
        // the type signature of this class is here so that we can build up more
        // robust Result types, but since this is the Ok class there are
        // actually never any instances of the error type E present as class
        // variables. Since there is no `E e` in this class, we can change the
        // error type through a cast without corrupting any data. This is also
        // more efficient than constructing a new `Ok<T, F>` object because we
        // are skipping a heap allocation; and since this class is immutable
        // there is no risk of the shared reference being corrupted. Corruption
        // could still theoretically occur if multiple threads try to mutate the
        // underlying `T t` simultaneously, but that would be a problem even if
        // we created a new `Ok<T, F>` on each invocation.
        // type erasure... боже мой...
        return (Ok<T, F>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F> Result<T, F> orElse(Function<E, Result<T, F>> resultFn) {
        return (Ok<T, F>) this;
    }
}
