package ai.embedding;

public interface EmbeddingProvider {
    float [] embed(String text);
}
