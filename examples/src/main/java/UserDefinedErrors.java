import dev.kylesilver.result.Result;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

public class UserDefinedErrors {

    interface ParserError {
        void logError();
    }

    @Value
    static class IncorrectNumberOfArguments implements ParserError {
        int expected;
        int actual;

        @Override
        public void logError() {
            System.out.printf("Expected %d arguments but received %d\n", expected, actual);
        }
    }

    @Value
    static class CouldNotParseToken implements ParserError {
        int position;
        String token;

        @Override
        public void logError() {
            System.out.printf("Could not parse token at position %d: \"%s\"\n", position, token);
        }
    }

    public static Result<int[], ParserError> parse(String input) {
        String[] tokens = input.split(",");
        if (tokens.length != 4) {
            return Result.err(new IncorrectNumberOfArguments(4, tokens.length));
        }
        int[] args = new int[4];
        for (int i = 0; i < args.length; i++) {
            try {
                args[i] = Integer.parseInt(tokens[i].trim());
            } catch (NumberFormatException e) {
                return Result.err(new CouldNotParseToken(input.indexOf(tokens[i]), tokens[i]));
            }
        }
        return Result.ok(args);
    }

    public static void main(String[] args) {
        List<String> tests = Arrays.asList("1, 2, 3", "1, 2, horse, 4", "1, 2, 3, 4");
        for (String test: tests) {
            parse(test).match(
                    ok -> System.out.printf("Ok: %s\n", Arrays.toString(ok)),
                    ParserError::logError
            );
        }
    }

}
