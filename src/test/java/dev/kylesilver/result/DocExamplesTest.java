package dev.kylesilver.result;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class DocExamplesTest {

    @Test
    public void testMatchDocExamples() {
        Result<List<Integer>, String> parsed = Result.ok(Arrays.asList(1,2,3,4));
        int validArgs = parsed.match(
                List::size,
                err -> {
                    System.out.println("could not parse args");
                    return 0;
                }
        );
        assertEquals(4, validArgs);
        parsed.match(
                ok -> {
                    System.out.println("ok " + ok.size());
                },
                err -> {
                    System.out.println("err " + err);
                }
        );
    }

    @Test
    public void testMapExamples() throws UnwrapException {
        assertEquals(6, Result.ok(5).map(x -> x + 1).unwrap());
        assertEquals("error", Result.<Integer, String>err("error").map(x -> x + 1).unwrapErr());
    }

    @Test
    public void testMapErrExamples() throws UnwrapException {
        assertEquals(
                "error: 'foo'",
                Result.err("foo").mapErr(err -> String.format("error: '%s'", err)).unwrapErr()
        );
        assertEquals(
                5,
                Result.ok(5).mapErr(err -> String.format("error: '%s'", err)).unwrap()
        );
    }

    @Test
    public void testAndExamples() {
        assertEquals(
                Result.err("first error"),
                Result.err("first error").and(Result.err("second error"))
        );
        assertEquals(
                Result.err("second error"),
                Result.ok(1).and(Result.err("second error"))
        );
    }

    @Test
    public void testAndThenExample() throws UnwrapException {
        Result<Integer, RuntimeException> result = Result.<String, RuntimeException>ok("hi")
                .andThen(ok -> Result.err(new NumberFormatException("foo")));
        assertInstanceOf(NumberFormatException.class, result.unwrapErr());
        Result<Result<Integer, NumberFormatException>, RuntimeException> awkward =
                Result.<String, RuntimeException>ok("hi").map(ok -> Result.err(new NumberFormatException("foo")));
        assertInstanceOf(NumberFormatException.class, awkward.unwrap().unwrapErr());
    }
}
