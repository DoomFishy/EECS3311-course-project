package ai.embedding;

public class CloudEmbedder implements EmbeddingProvider {
    @Override
    public float[] embed(String text) {
        System.out.println("[CloudEmbedder] Sending text to cloud for embedding: " + text);
        // Return a dummy 3D vector
        return new float[]{0.9f, 0.8f, 0.7f};
    }
}