package ai.factory;

import ai.embedding.CloudEmbedder;
import ai.embedding.EmbeddingProvider;
import ai.llm.CloudllmSource;
import ai.llm.llmSource;
import ai.vector.CloudVectorStore;
import ai.vector.VectorStore;

public class OnlineFactory implements AIFactory {

    @Override
    public llmSource createLlmSource() {
        return new CloudllmSource();
    }

    @Override
    public EmbeddingProvider createEmbeddings() {
        return new CloudEmbedder();
    }

    @Override
    public VectorStore createVectorStore() {
        return new CloudVectorStore();
    }
}
