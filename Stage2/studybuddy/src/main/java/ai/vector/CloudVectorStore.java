package ai.vector;

import java.util.ArrayList;
import java.util.List;

public class CloudVectorStore implements VectorStore {
    @Override
    public void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Error - CloudVectorStore: id required");
        }
        System.out.println("[CloudVectorStore] Sending DELETE request to cloud for ID: " + id);
    }

    @Override
    public List<Chunk> query(float[] vector, int index) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Error - CloudVectorStore: vector required");
        }
        if (index <= 0) {
            throw new IllegalArgumentException("Error - CloudVectorStore: index must be > 0");
        }

        System.out.println("[CloudVectorStore] Sending QUERY request to cloud for top " + index);
        List<Chunk> results = new ArrayList<>();
        results.add(new Chunk("This is a cloud mock result.", vector));
        return results;
    }

    @Override
    public void upsert(String id, float[] vector) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Error - CloudVectorStore: id required");
        }
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Error - CloudVectorStore: vector required");
        }
        System.out.println("[CloudVectorStore] Sending UPSERT request to cloud for ID: " + id);
    }
}