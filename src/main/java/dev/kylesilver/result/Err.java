package dev.kylesilver.result;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ToString
@EqualsAndHashCode
public class Err<T, E> implements Result<T, E> {
    private final E e;

    public Err(@NotNull E e) {
        this.e = e;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public Optional<T> ok() {
        return Optional.empty();
    }

    @Override
    public Optional<E> err() {
        return Optional.of(e);
    }

    @Override
    public T unwrap() throws UnwrapException {
        throw new UnwrapException("The Result was expected to be Ok, but was instead an Error type with value " + e);
    }

    @Override
    public E unwrapErr() {
        return e;
    }

    @Override
    public T expect(String errorMessage) throws UnwrapException {
        throw new UnwrapException(errorMessage);
    }

    @Override
    public <F extends Throwable> T expect(Function<E, F> mapping) throws F {
        throw mapping.apply(e);
    }

    @Override
    public E expectErr(String errorMessage) throws UnwrapException {
        return e;
    }

    @Override
    public <F extends Throwable> E expectErr(Function<T, F> mapping) throws F {
        return e;
    }

    @Override
    public <U> U match(Function<T, U> ifOk, Function<E, U> ifErr) {
        return ifErr.apply(e);
    }

    @Override
    public void match(Consumer<T> ifOk, Consumer<E> ifErr) {
        ifErr.accept(e);
    }

    @Override
    public <U> Result<U, E> map(Function<T, U> mapping) {
        return Result.err(e);
    }

    @Override
    public <F> Result<T, F> mapErr(Function<E, F> mapping) {
        return Result.err(mapping.apply(e));
    }
}
