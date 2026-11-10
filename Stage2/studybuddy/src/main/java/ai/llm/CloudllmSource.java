package ai.llm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CloudllmSource implements llmSource {
    private static final String DEFAULT_MODEL = System.getenv().getOrDefault("OPENROUTER_MODEL", "openrouter/free");
    private static final String DEFAULT_URL = System.getenv().getOrDefault("OPENROUTER_URL", "https://openrouter.ai/api/v1/chat/completions");

    @Override
    public String run(String context) {
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("Error - CloudllmSource: context required");
        }

        String apiKey = System.getenv("OPENROUTER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENROUTER_API_KEY is not set. Add it to your environment or .env file.");
        }

        String model = System.getenv().getOrDefault("OPENROUTER_MODEL", DEFAULT_MODEL);
        String url = System.getenv().getOrDefault("OPENROUTER_URL", DEFAULT_URL);
        String jsonBody = String.format("""
            {
              "model": "%s",
              "messages": [{"role": "user", "content": "%s"}]
            }
            """, model, escapeForJson(context));

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://studybuddy.local")
                    .header("X-Title", "StudyBuddy")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (response.statusCode() >= 400) {
                throw new IllegalStateException("OpenRouter request failed (" + response.statusCode() + "): " + body);
            }

            String content = extractJsonString(body, "content");
            if (content == null || content.isBlank()) {
                throw new IllegalStateException("OpenRouter response did not include model output. Body: " + body);
            }

            return content;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Cloud LLM request failed: " + e.getMessage(), e);
        }
    }

    private static String extractJsonString(String json, String key) {
        if (json == null || json.isBlank()) {
            return null;
        }

        String token = "\"" + key + "\"";
        int keyIndex = json.indexOf(token);
        if (keyIndex < 0) {
            return null;
        }

        int colon = json.indexOf(':', keyIndex + token.length());
        if (colon < 0) {
            return null;
        }

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length() || json.charAt(valueStart) != '"') {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = valueStart + 1; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (escaped) {
                switch (ch) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(ch); break;
                }
                escaped = false;
                continue;
            }

            if (ch == '\\') {
                escaped = true;
                continue;
            }

            if (ch == '"') {
                return sb.toString();
            }

            sb.append(ch);
        }

        return null;
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

    private static String unescapeJson(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                switch (next) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(next); break;
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}