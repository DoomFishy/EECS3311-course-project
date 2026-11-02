package ai.embedding;

public class LocalEmbedder implements EmbeddingProvider {
    @Override
    public float[] embed(String text) {
        System.out.println("[LocalEmbedder] Embedding text locally: " + text);
        // Return a dummy 3D vector
        return new float[]{0.1f, 0.2f, 0.3f};
    }
}