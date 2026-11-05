package ai.factory;

import ai.embedding.EmbeddingProvider;
import ai.embedding.LocalEmbedder;
import ai.llm.LocalllmSource;
import ai.llm.llmSource;
import ai.vector.LocalVectorStore;
import ai.vector.VectorStore;

public class OfflineFactory implements AIFactory {

    @Override
    public llmSource createLlmSource() {
        return new LocalllmSource();
    }

    @Override
    public EmbeddingProvider createEmbeddings() {
        return new LocalEmbedder();
    }

    @Override
    public VectorStore createVectorStore() {
        return new LocalVectorStore();
    }
}
