package dev.kylesilver.result;

/**
 * This exception is thrown when the {@link Result#tryOr Result.tryOr} function is unable to
 * cast a caught exception from the caller-provided {@link CheckedSupplier} into
 * the desired error type. The existence of this class violates the principle of
 * least-surprise insofar as it is unchecked and can blow up during runtime,
 * which runs a bit counter to the idea that the {@link Result} class wraps and
 * simplifies error handling. Unfortunately the existence of unchecked
 * exceptions means that any java function can blow up randomly for any reason,
 * so this is ultimately a losing battle. The only way to avoid ever
 * encountering this exception is to pass {@link Throwable} as the expected type
 * to {@link Result#tryOr}, a move that would make any wrapped exceptions so
 * opaque that they would be practically meaningless.
 */
public class ErrorTypeMismatchException extends RuntimeException {
    public ErrorTypeMismatchException(Class<? extends Throwable> expected, Throwable actual) {
        super(errorMessage(expected, actual), actual);
    }

    private static String errorMessage(Class<? extends Throwable> expected, Throwable actual) {
        return String.format(
                "Expected an exception which could be cast to %s but instead caught an exception with type %s",
                expected,
                actual.getClass()
        );
    }
}
