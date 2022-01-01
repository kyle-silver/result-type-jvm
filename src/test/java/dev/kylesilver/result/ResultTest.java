package dev.kylesilver.result;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    public void testIsOk() {
        assertTrue(Result.ok(1).isOk());
        assertFalse(Result.ok(1).isErr());
    }

    @Test
    public void testIsErr() {
        assertTrue(Result.err(1).isErr());
        assertFalse(Result.err(1).isOk());
    }

    @Test
    public void testOk() {
        assertInstanceOf(Ok.class, Result.ok(1));
    }

    @Test
    public void testErr() {
        assertInstanceOf(Err.class, Result.err(1));
    }

    @Test
    public void testGetOk() {
        assertEquals(Optional.empty(), Result.err(1).ok());
        assertEquals(Optional.of(1), Result.ok(1).ok());
    }

    @Test
    public void testGetErr() {
        assertEquals(Optional.empty(), Result.ok(1).err());
        assertEquals(Optional.of(1), Result.err(1).err());
    }

    @Test
    public void testUnwrap() {
        assertEquals(1, Result.ok(1).unwrap());
        assertThrows(UnwrapException.class, () -> Result.err(1).unwrap());
    }

    @Test
    public void testUnwrapErr() {
        assertEquals(1, Result.err(1).unwrapErr());
        assertThrows(UnwrapException.class, () -> Result.ok(1).unwrapErr());
    }

    @Test
    public void testExpect() throws UnwrapException {
        // user-provided message
        assertEquals(1, Result.ok(1).expect("wasn't ok"));
        var unwrapException = assertThrows(
                UnwrapException.class,
                () -> Result.err(1).expect("wasn't ok")
        );
        assertEquals("wasn't ok", unwrapException.getMessage());

        // custom exception
        assertEquals(1, Result.ok(1).expect(e -> new IllegalArgumentException("foo")));
        var illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> Result.err(1).expect(e -> new IllegalArgumentException(e.toString()))
        );
        assertEquals("1", illegalArgumentException.getMessage());
    }

    @Test
    public void testExpectErr() throws UnwrapException {
        // user-provided message
        assertEquals(1, Result.err(1).expectErr("wasn't ok"));
        var unwrapException = assertThrows(
                UnwrapException.class,
                () -> Result.ok(1).expectErr("wasn't err")
        );
        assertEquals("wasn't err", unwrapException.getMessage());

        // custom exception
        assertEquals(1, Result.err(1).expectErr(e -> new IllegalArgumentException("foo")));
        var illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> Result.ok(1).expectErr(e -> new IllegalArgumentException(e.toString()))
        );
        assertEquals("1", illegalArgumentException.getMessage());
    }

    @Test
    public void testMatch() {
        // test match when it maps values
        assertEquals(Integer.valueOf(2), Result.ok(1).match(
                ok -> ok + 1,
                err -> -1
        ));
        assertEquals("bar", Result.err(1).match(
                ok -> "foo",
                err -> "bar"
        ));
        // test match when it produces side effects
        AtomicInteger count = new AtomicInteger(2);
        Result.ok(1).match(
                ok -> count.addAndGet(1),
                err -> {}
        );
        assertEquals(3, count.get());
        Result.err(1).match(
                ok -> {},
                err -> count.addAndGet(-1)
        );
        assertEquals(2, count.get());
    }

    @Test
    public void testMap() throws UnwrapException {
        assertEquals("2", Result.ok(1).map(v -> v + 1).map(String::valueOf).unwrap());
        Result<Integer, Integer> result = Result.err(1);
        Result<String, Integer> mapped = result.map(v -> v + 1).map(String::valueOf);
        assertEquals(1, mapped.unwrapErr());
    }

    @Test
    public void testMapErr() throws UnwrapException {
        assertEquals("2", Result.err(1).mapErr(v -> v + 1).mapErr(String::valueOf).unwrapErr());
        Result<Integer, Integer> result = Result.ok(1);
        Result<Integer, String> mapped = result.mapErr(v -> v + 1).mapErr(String::valueOf);
        assertEquals(1, mapped.unwrap());
    }

    @Test
    public void testAnd() {
        assertEquals(Result.err(1), Result.ok(0).and(Result.err(1)));
        assertEquals(Result.ok(1), Result.ok(0).and(Result.ok(1)));
        assertEquals(Result.err(0), Result.err(0).and(Result.err(1)));
        assertEquals(Result.ok(1), Result.ok(0).and(Result.ok(1)));
    }

    @Test
    public void testAndThen() {
        assertEquals(Result.ok(1), Result.ok(0).andThen(ok -> Result.ok(ok + 1)));
        assertEquals(Result.err(1), Result.ok(0).andThen(ok -> Result.err(ok + 1)));
        assertEquals(Result.err(0), Result.err(0).andThen(Result::ok));
    }

    @Test
    public void testOr() {
        assertEquals(Result.ok(0), Result.ok(0).or(Result.ok(1)));
        assertEquals(Result.ok(1), Result.err(0).or(Result.ok(1)));
        assertEquals(Result.err(1), Result.err(0).or(Result.err(1)));
    }

    @Test
    public void testOrElse() {
        assertEquals(Result.ok(1), Result.ok(1).orElse(err -> Result.ok(0)));
        assertEquals(Result.ok(1), Result.err(0).orElse(err -> Result.ok(1)));
        assertEquals(Result.err(1), Result.err(0).orElse(err -> Result.err(1)));
    }
}