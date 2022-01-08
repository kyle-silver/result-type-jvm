import dev.kylesilver.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadFileToString {

    public static Result<String, IOException> readFileToString(Path path) {
        return Result.tryOr(() -> Files.readString(path), IOException.class);
    }

    public static void main(String[] args) {
        // prints the contents of this file
        Path path = Paths.get("src", "main", "java", "ReadFileToString.java");
        System.out.println(readFileToString(path).ok().orElse("Error!"));
        // prints an error
        System.out.println(readFileToString(Paths.get("fake.txt")));
    }
}
