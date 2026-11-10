package ai.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFReaderTool implements Tool {

    @Override
    public String execute(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Error - PDFReaderTool: input path required");
        }

        Path path = Path.of(input);
        if (!Files.exists(path)) {
            return "";
        }

        try (PDDocument doc = PDDocument.load(new File(input))) {
            String text = new PDFTextStripper().getText(doc);
            return text == null ? "" : text.trim();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Error - PDFReaderTool: unable to read PDF: " + input, e);
        }
    }

    @Override
    public boolean supports(String type) {
        if (type == null) {
            return false;
        }
        return type.toLowerCase(Locale.ROOT).endsWith(".pdf");
    }
}