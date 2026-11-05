package ai.tool;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileReaderTool implements Tool {

    @Override
    public String execute(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Error - FileReaderTool: input path required");
        }

        try {
            Path path = Path.of(input);
            if (!Files.exists(path)) {
                return "File not found: " + input;
            }
            return Files.readString(path);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error - FileReaderTool: unable to read file: " + input, e);
        }
    }

    @Override
    public boolean supports(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.toLowerCase();
        return normalized.endsWith(".txt") || normalized.endsWith(".md") || normalized.endsWith(".csv");
    }
}
