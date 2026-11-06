package ai.tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFReaderTool implements Tool {
    private static final Pattern PDF_TEXT_PATTERN = Pattern.compile("\\(([^\\)\\r\\n]{1,200})\\)");

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
            String extracted = extractText(data);
            if (extracted != null && !extracted.isBlank()) {
                return extracted.trim();
            }

            return "This PDF was uploaded, but no readable text could be extracted from the file contents.";
        } catch (IOException e) {
            throw new IllegalArgumentException("Error - PDFReaderTool: unable to read PDF: " + input, e);
        }
    }

    @Override
    public boolean supports(String type) {
        if (type == null) {
            return false;
        }
        return type.toLowerCase(Locale.ROOT).endsWith(".pdf");
    }

    private String extractText(byte[] pdfBytes) {
        try {
            String raw = new String(pdfBytes, StandardCharsets.ISO_8859_1);
            List<String> textParts = new ArrayList<>();
            Matcher matcher = PDF_TEXT_PATTERN.matcher(raw);

            while (matcher.find()) {
                String candidate = matcher.group(1).replace("\\n", " ").replace("\\r", " ").replace("\\t", " ");
                candidate = candidate.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]+", " ");
                candidate = candidate.replaceAll("\\s+", " ").trim();
                if (!candidate.isBlank() && candidate.length() > 1) {
                    textParts.add(candidate);
                }
            }

            if (textParts.isEmpty()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (String part : textParts) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(part);
            }
            return sb.toString();
        } catch (Exception ignored) {
            return "";
        }
    }
}