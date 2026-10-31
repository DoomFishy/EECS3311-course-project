package ai.vector;

import java.util.List;

public interface VectorStore {
    void delete(String id);
    
    List<Chunk> query(float[] vector, int index);
    
    void upsert(String id, float[] vector);
}
