package ai.embedding;

public class CloudEmbedder implements EmbeddingProvider {
    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Error - CloudEmbedder: text required");
        }

        System.out.println("[CloudEmbedder] Sending text to cloud for embedding: " + text);
        return new float[]{0.9f, 0.8f, 0.7f};
    }
}