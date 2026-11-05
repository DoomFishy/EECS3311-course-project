package ai.factory;

import ai.embedding.*;
import ai.llm.*;
import ai.vector.*;

public interface AIFactory {
    
    llmSource createLlmSource();
    EmbeddingProvider createEmbeddings();
    VectorStore createVectorStore();

    static AIFactory createDefault() {
        String mode = System.getProperty("studybuddy.ai.mode", System.getenv().getOrDefault("STUDYBUDDY_AI_MODE", "auto"));
        if ("local".equalsIgnoreCase(mode)) {
            return new OfflineFactory();
        }
        if ("cloud".equalsIgnoreCase(mode) || "openrouter".equalsIgnoreCase(mode)) {
            return new OnlineFactory();
        }

        String ollamaHost = System.getenv().getOrDefault("OLLAMA_HOST", "http://localhost:11434");
        boolean ollamaAvailable = isOllamaReachable(ollamaHost);
        if (ollamaAvailable) {
            return new OfflineFactory();
        }

        return new OnlineFactory();
    }

    static boolean isOllamaReachable(String host) {
        try {
            String url = host.replaceAll("/+$", "") + "/api/tags";
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(3))
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 500;
        } catch (Exception e) {
            return false;
        }
    }
}
