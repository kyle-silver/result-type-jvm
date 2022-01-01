package dev.kylesilver.result;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public T expect(String errorMessage) throws UnwrapException {
        return t;
    }

    @Override
    public <F extends Throwable> T expect(Function<E, F> mapping) throws F {
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
}
