package ai.vector;

import java.util.ArrayList;
import java.util.List;

public class LocalVectorStore implements VectorStore {
    @Override
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Error - LocalVectorStore: id required");
        }
        System.out.println("[LocalVectorStore] Deleting ID: " + id);
    }

    @Override
    public List<Chunk> query(float[] vector, int index) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Error - LocalVectorStore: vector required");
        }
        if (index <= 0) {
            throw new IllegalArgumentException("Error - LocalVectorStore: index must be > 0");
        }

        System.out.println("[LocalVectorStore] Querying top " + index + " results.");
        List<Chunk> results = new ArrayList<>();
        results.add(new Chunk("This is a local mock result.", vector));
        return results;
    }

    @Override
    public void upsert(String id, float[] vector) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Error - LocalVectorStore: id required");
        }
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Error - LocalVectorStore: vector required");
        }
        System.out.println("[LocalVectorStore] Upserting ID: " + id + " with vector size: " + vector.length);
    }
}