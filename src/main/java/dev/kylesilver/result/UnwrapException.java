package dev.kylesilver.result;

import java.util.function.Function;

/**
 * This exception is thrown when a value is unwrapped. Unwrapping means that the
 * {@link Result} was not what was expected and should be treated as fatal. It
 * is a checked exception to discourage the practice of catching unwraps. To
 * avoid muddying your method signatures with this exception, consider supplying
 * a default value or coming up with a way to gracefully handle exceptions. You
 * can also use the {@link Result#expect(Function)} method to throw a custom
 * error instead of this one.
 */
public class UnwrapException extends Exception {
    public UnwrapException(String s) {
        super(s);
    }
}
