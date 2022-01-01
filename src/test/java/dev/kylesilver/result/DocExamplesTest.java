package dev.kylesilver.result;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
