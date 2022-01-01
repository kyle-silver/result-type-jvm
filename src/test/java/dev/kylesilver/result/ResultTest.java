package dev.kylesilver.result;

import org.junit.jupiter.api.Test;

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
    public void testExpect() {
        // user-provided message
        assertEquals(1, Result.ok(1).expect("wasn't ok"));
        try {
            Result.err(1).expect("wasn't ok");
            fail();
        } catch (UnwrapException e) {
            assertEquals("wasn't ok", e.getMessage());
        }
        // custom exception
        assertEquals(1, Result.ok(1).expect(e -> new IllegalArgumentException("foo")));
        try {
            Result.err(1).expect(e -> new IllegalArgumentException(e.toString()));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("1", e.getMessage());
        }
    }

    @Test
    public void testExpectErr() {
        // user-provided message
        assertEquals(1, Result.err(1).expectErr("wasn't ok"));
        try {
            Result.ok(1).expectErr("wasn't ok");
            fail();
        } catch (UnwrapException e) {
            assertEquals("wasn't ok", e.getMessage());
        }
        // custom exception
        assertEquals(1, Result.err(1).expectErr(e -> new IllegalArgumentException("foo")));
        try {
            Result.ok(1).expectErr(e -> new IllegalArgumentException(e.toString()));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("1", e.getMessage());
        }
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
    public void testMap() {
        assertEquals("2", Result.ok(1).map(v -> v + 1).map(String::valueOf).unwrap());
        Result<Integer, Integer> result = Result.err(1);
        Result<String, Integer> mapped = result.map(v -> v + 1).map(String::valueOf);
        assertEquals(1, mapped.unwrapErr());
    }

    @Test
    public void testMapErr() {
        assertEquals("2", Result.err(1).mapErr(v -> v + 1).mapErr(String::valueOf).unwrapErr());
        Result<Integer, Integer> result = Result.ok(1);
        Result<Integer, String> mapped = result.mapErr(v -> v + 1).mapErr(String::valueOf);
        assertEquals(1, mapped.unwrap());
    }
}