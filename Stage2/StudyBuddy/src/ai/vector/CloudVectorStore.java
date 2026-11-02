package ai.vector;
import java.util.ArrayList;
import java.util.List;

public class CloudVectorStore implements VectorStore {
    @Override
    public void delete(String id) {
        System.out.println("[CloudVectorStore] Sending DELETE request to cloud for ID: " + id);
    }

    @Override
    public List<Chunk> query(float[] vector, int index) {
        System.out.println("[CloudVectorStore] Sending QUERY request to cloud for top " + index);
        List<Chunk> results = new ArrayList<>();
        results.add(new Chunk("This is a cloud mock result.", vector));
        return results;
    }

    @Override
    public void upsert(String id, float[] vector) {
        System.out.println("[CloudVectorStore] Sending UPSERT request to cloud for ID: " + id);
    }
}