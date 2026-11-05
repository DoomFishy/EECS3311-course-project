package ai.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PDFReaderTool implements Tool {

    @Override
    public String execute(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Error - PDFReaderTool: input path required");
        }

        try {
            Path path = Path.of(input);
            if (!Files.exists(path)) {
                return "PDF not found: " + input;
            }

            byte[] data = Files.readAllBytes(path);
            return "PDF loaded successfully: " + path.getFileName() + " (" + data.length + " bytes)";
        } catch (IOException e) {
            throw new IllegalArgumentException("Error - PDFReaderTool: unable to read PDF: " + input, e);
        }
    }

    @Override
    public boolean supports(String type) {
        if (type == null) {
            return false;
        }
        return type.toLowerCase().endsWith(".pdf");
    }
}
