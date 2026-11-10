package ai.agent;

import ai.embedding.EmbeddingProvider;
import ai.vector.Chunk;
import ai.vector.VectorStore;
import java.util.List;

public class EmbeddingTool {
    private final EmbeddingProvider embedder;
    private final VectorStore vectorStore;

    public EmbeddingTool(EmbeddingProvider embedder, VectorStore vectorStore) {
        if (embedder == null) {
            throw new IllegalArgumentException("Error - EmbeddingTool: embedder required");
        }
        if (vectorStore == null) {
            throw new IllegalArgumentException("Error - EmbeddingTool: vectorStore required");
        }
        this.embedder = embedder;
        this.vectorStore = vectorStore;
    }

    public float[] embed(String text) {
        return embedder.embed(text);
    }

    public List<Chunk> query(float[] vector, int index) {
        return vectorStore.query(vector, index);
    }

    public void upsert(String id, float[] vector) {
        vectorStore.upsert(id, vector);
    }
}
