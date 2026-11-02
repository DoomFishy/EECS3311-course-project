package ai.vector;
import java.util.ArrayList;
import java.util.List;

public class LocalVectorStore implements VectorStore {
    @Override
    public void delete(String id) {
        System.out.println("[LocalVectorStore] Deleting ID: " + id);
    }

    @Override
    public List<Chunk> query(float[] vector, int index) {
        System.out.println("[LocalVectorStore] Querying top " + index + " results.");
        // Return a dummy result for now
        List<Chunk> results = new ArrayList<>();
        results.add(new Chunk("This is a local mock result.", vector));
        return results;
    }

    @Override
    public void upsert(String id, float[] vector) {
        System.out.println("[LocalVectorStore] Upserting ID: " + id + " with vector size: " + vector.length);
    }
}