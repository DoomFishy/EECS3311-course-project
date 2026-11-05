package ai.embedding;

public class LocalEmbedder implements EmbeddingProvider {
    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Error - LocalEmbedder: text required");
        }

        System.out.println("[LocalEmbedder] Embedding text locally: " + text);
        return new float[]{0.1f, 0.2f, 0.3f};
    }
}