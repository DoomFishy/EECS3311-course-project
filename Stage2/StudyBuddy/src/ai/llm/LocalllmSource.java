package ai.llm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LocalllmSource implements llmSource {
    private static final String DEFAULT_HOST = System.getenv().getOrDefault("OLLAMA_HOST", "http://localhost:11434");
    private static final String DEFAULT_MODEL = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2");

    @Override
    public String run(String context) {
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("Error - LocalllmSource: context required");
        }

        String host = System.getenv().getOrDefault("OLLAMA_HOST", DEFAULT_HOST);
        String model = System.getenv().getOrDefault("OLLAMA_MODEL", DEFAULT_MODEL);
        String url = host.replaceAll("/+$", "") + "/api/generate";

        String jsonBody = String.format("""
            {
              "model": "%s",
              "prompt": "%s",
              "stream": false
            }
            """, model, escapeForJson(context));

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Ollama request failed (" + response.statusCode() + "): " + body);
            }

            String generated = extractResponseText(body);
            if (generated == null || generated.isBlank()) {
                throw new IllegalStateException("Ollama response was empty. Body: " + body);
            }

            return generated;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Local Ollama request failed: " + e.getMessage(), e);
        }
    }

    private static String extractResponseText(String json) {
        int start = json.indexOf("\"response\"");
        if (start < 0) {
            return null;
        }
        int colon = json.indexOf(':', start);
        if (colon < 0) {
            return null;
        }
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) {
            return null;
        }
        int lastQuote = json.lastIndexOf('"');
        if (lastQuote <= firstQuote) {
            return null;
        }
        String raw = json.substring(firstQuote + 1, lastQuote);
        return raw.replace("\\n", "\n").replace("\\\"", "\"");
    }

    private static String escapeForJson(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(ch);
            }
        }
        return sb.toString();
    }
}